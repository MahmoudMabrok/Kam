// Kam — cost-of-living dataset. NYC overall index = 100 baseline.
// perUSD = local currency units per 1 USD (approx). rent = rent-only index.
const CITIES = [
  { id:'nyc', city:'New York',     country:'United States', cc:'US', cur:'USD', sym:'$',  perUSD:1,    index:100, rent:100 },
  { id:'sfo', city:'San Francisco',country:'United States', cc:'US', cur:'USD', sym:'$',  perUSD:1,    index:96,  rent:104 },
  { id:'ldn', city:'London',       country:'United Kingdom',cc:'GB', cur:'GBP', sym:'£',  perUSD:0.79, index:80,  rent:88  },
  { id:'ams', city:'Amsterdam',    country:'Netherlands',   cc:'NL', cur:'EUR', sym:'€',  perUSD:0.92, index:73,  rent:76  },
  { id:'syd', city:'Sydney',       country:'Australia',     cc:'AU', cur:'AUD', sym:'A$', perUSD:1.52, index:77,  rent:80  },
  { id:'sin', city:'Singapore',    country:'Singapore',     cc:'SG', cur:'SGD', sym:'S$', perUSD:1.35, index:83,  rent:95  },
  { id:'tor', city:'Toronto',      country:'Canada',        cc:'CA', cur:'CAD', sym:'C$', perUSD:1.37, index:71,  rent:72  },
  { id:'ber', city:'Berlin',       country:'Germany',       cc:'DE', cur:'EUR', sym:'€',  perUSD:0.92, index:66,  rent:60  },
  { id:'bcn', city:'Barcelona',    country:'Spain',         cc:'ES', cur:'EUR', sym:'€',  perUSD:0.92, index:55,  rent:52  },
  { id:'lis', city:'Lisbon',       country:'Portugal',      cc:'PT', cur:'EUR', sym:'€',  perUSD:0.92, index:53,  rent:50  },
  { id:'tyo', city:'Tokyo',        country:'Japan',         cc:'JP', cur:'JPY', sym:'¥',  perUSD:157,  index:68,  rent:58  },
  { id:'dxb', city:'Dubai',        country:'UAE',           cc:'AE', cur:'AED', sym:'د.إ',perUSD:3.67, index:65,  rent:74  },
  { id:'ist', city:'Istanbul',     country:'Türkiye',       cc:'TR', cur:'TRY', sym:'₺',  perUSD:32,   index:34,  rent:32  },
  { id:'mex', city:'Mexico City',  country:'Mexico',        cc:'MX', cur:'MXN', sym:'$',  perUSD:18,   index:46,  rent:40  },
  { id:'bkk', city:'Bangkok',      country:'Thailand',      cc:'TH', cur:'THB', sym:'฿',  perUSD:36,   index:43,  rent:38  },
  { id:'blr', city:'Bengaluru',    country:'India',         cc:'IN', cur:'INR', sym:'₹',  perUSD:83,   index:30,  rent:24  },
  { id:'cai', city:'Cairo',        country:'Egypt',         cc:'EG', cur:'EGP', sym:'E£', perUSD:48,   index:26,  rent:20  },
];

const CITY = Object.fromEntries(CITIES.map(c => [c.id, c]));

// Each category maps the city's overall index through an affine curve
// (base + slope·(index−base)). Different intercepts make category gaps
// between two cities differ realistically — not a flat single percentage.
const CATS = [
  { key:'rent',      label:'Rent',       base:null, slope:null }, // explicit rent index
  { key:'groceries', label:'Groceries',  base:40,   slope:0.78 },
  { key:'dining',    label:'Dining out', base:22,   slope:1.25 },
  { key:'transport', label:'Transport',  base:35,   slope:0.62 },
  { key:'leisure',   label:'Leisure',    base:30,   slope:1.05 },
];

function catIndex(c, cat){
  if(cat.key==='rent') return c.rent;
  return cat.base + (c.index - cat.base) * cat.slope;
}

const TIERS = [
  { key:'basic',       label:'Basic',       max:2500,  blurb:'Covers the essentials with little left over.' },
  { key:'middle',      label:'Middle',      max:5000,  blurb:'Steady living, modest savings and the odd treat.' },
  { key:'comfortable', label:'Comfortable', max:9000,  blurb:'Room to save, dine out and travel freely.' },
  { key:'luxury',      label:'Luxury',      max:Infinity, blurb:'Premium living with significant disposable income.' },
];

// fmt a number into a currency-ish string
function money(n, city, { decimals=0 } = {}){
  const v = Math.round(n / (decimals?1:1));
  return city.sym + Number(v.toFixed(decimals)).toLocaleString('en-US',
    { minimumFractionDigits:decimals, maximumFractionDigits:decimals });
}

// core comparison math. salaryLocal is monthly, in origin currency.
// offerLocal (optional) is a salary offer in the DEST city's currency.
function compare(origin, dest, salaryLocal, offerLocal=0){
  const salaryUSD = salaryLocal / origin.perUSD;
  const breakevenUSD = salaryUSD * (dest.index / origin.index);
  const breakevenLocal = breakevenUSD * dest.perUSD;
  const sameSalaryLocal = salaryUSD * dest.perUSD;          // identical real money, dest currency
  const powerRatio = origin.index / dest.index;            // >1 = goes further
  const powerPct = (powerRatio - 1) * 100;
  const costDeltaPct = (dest.index - origin.index) / origin.index * 100; // <0 = cheaper
  const adjustedUSD = salaryUSD * (100 / dest.index);      // NYC-equivalent buying power
  const tier = TIERS.find(t => adjustedUSD < t.max) || TIERS[TIERS.length-1];
  const tierIndex = TIERS.indexOf(tier);
  const clampPos = usd => Math.max(0.04, Math.min(0.96, usd / 13000));
  const tierPos = clampPos(adjustedUSD);
  // your current lifestyle, at home (ghost reference on the tier meter)
  const homeAdjUSD = salaryUSD * (100 / origin.index);
  const homeTierPos = clampPos(homeAdjUSD);
  let offer = null;
  if(offerLocal > 0){
    const offerUSD = offerLocal / dest.perUSD;
    const vsBreakevenPct = (offerUSD - breakevenUSD) / breakevenUSD * 100;
    const equivalentHomeLocal = offerUSD * (origin.index / dest.index) * origin.perUSD;
    const offerAdjUSD = offerUSD * (100 / dest.index);
    const oTier = TIERS.find(t => offerAdjUSD < t.max) || TIERS[TIERS.length-1];
    offer = { offerUSD, vsBreakevenPct, equivalentHomeLocal,
              tier:oTier, tierIndex:TIERS.indexOf(oTier), tierPos:clampPos(offerAdjUSD) };
  }
  return { salaryUSD, breakevenUSD, breakevenLocal, sameSalaryLocal,
           powerRatio, powerPct, costDeltaPct, adjustedUSD, tier, tierIndex, tierPos,
           homeTierPos, offer };
}

Object.assign(window, { CITIES, CITY, CATS, catIndex, TIERS, money, compare });

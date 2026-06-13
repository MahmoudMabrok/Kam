// Kam — main app
const { useState, useEffect, useRef } = React;

function Wordmark(){
  return (
    <div className="wordmark">
      <span className="wm-dot" />
      Kam
    </div>
  );
}

function CitySelector({ label, city, onTap }){
  return (
    <button className="city-sel" onClick={onTap}>
      <div className="city-sel-label">{label}</div>
      <div className="city-sel-main">
        <RegionChip cc={city.cc} />
        <div className="city-sel-text">
          <div className="city-sel-name">{city.city}</div>
          <div className="city-sel-country">{city.country}</div>
        </div>
        <svg className="city-sel-chev" width="11" height="18" viewBox="0 0 11 18"><path d="M2 2l7 7-7 7" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/></svg>
      </div>
    </button>
  );
}

function HomeScreen({ origin, dest, salary, offer, onPick, onSwap, onEditSalary, onEditOffer, onClearOffer, onCompare }){
  const live = compare(origin, dest, salary, offer);
  const cheaper = live.costDeltaPct < 0;
  const hasOffer = offer > 0 && live.offer;
  return (
    <div className="screen home">
      <header className="top">
        <Wordmark />
        <button className="icon-btn" aria-label="Settings">
          <svg width="20" height="20" viewBox="0 0 20 20"><circle cx="10" cy="10" r="2.4" fill="none" stroke="currentColor" strokeWidth="1.7"/><path d="M10 1.5v2.4M10 16.1v2.4M3 10H.6M19.4 10H17M4.6 4.6l1.7 1.7M13.7 13.7l1.7 1.7M15.4 4.6l-1.7 1.7M6.3 13.7l-1.7 1.7" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round"/></svg>
        </button>
      </header>

      <h1 className="hero">How far does your salary <em>travel?</em></h1>
      <p className="hero-sub">Compare living costs and real purchasing power between any two cities.</p>

      <div className="compare-card">
        <div className="cc-row">
          <CitySelector label="FROM" city={origin} onTap={()=>onPick('origin')} />
        </div>

        <button className="salary-field" onClick={onEditSalary}>
          <div className="sf-label">Your monthly salary</div>
          <div className="sf-value">
            <span className="sf-amount">{money(salary, origin)}</span>
            <span className="sf-cur">{origin.cur}/mo</span>
          </div>
          <div className="sf-edit">Edit</div>
        </button>

        <div className="cc-divider">
          <button className="swap-btn" onClick={onSwap} aria-label="Swap cities">
            <svg width="20" height="20" viewBox="0 0 20 20"><path d="M6 3v11M6 14l-3-3M6 14l3-3M14 17V6M14 6l-3 3M14 6l3 3" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"/></svg>
          </button>
        </div>

        <div className="cc-row">
          <CitySelector label="TO" city={dest} onTap={()=>onPick('dest')} />
        </div>

        {hasOffer ? (
          <div className="offer-field">
            <button className="of-main" onClick={onEditOffer}>
              <div className="sf-label of-label">Salary offer in {dest.city}</div>
              <div className="of-value">{money(offer, dest)}<span className="of-cur">{dest.cur}/mo</span></div>
            </button>
            <button className="of-clear" onClick={onClearOffer} aria-label="Remove offer">
              <svg width="12" height="12" viewBox="0 0 12 12"><path d="M2 2l8 8M10 2l-8 8" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round"/></svg>
            </button>
          </div>
        ) : (
          <button className="offer-add" onClick={onEditOffer}>+ Compare a salary offer in {dest.city}</button>
        )}

        {hasOffer ? (
          <div className={'live-strip '+(live.offer.vsBreakevenPct>=0?'down':'up')}>
            <span className="ls-arrow">{live.offer.vsBreakevenPct>=0?'↑':'↓'}</span>
            <span className="ls-text">
              Offer is <strong>{Math.abs(Math.round(live.offer.vsBreakevenPct))}% {live.offer.vsBreakevenPct>=0?'above':'below'}</strong> your breakeven
            </span>
          </div>
        ) : (
          <div className={'live-strip '+(cheaper?'down':'up')}>
            <span className="ls-arrow">{cheaper?'↓':'↑'}</span>
            <span className="ls-text">
              {dest.city} is <strong>{Math.abs(Math.round(live.costDeltaPct))}% {cheaper?'cheaper':'pricier'}</strong>
            </span>
          </div>
        )}
      </div>

      <button className="cta" onClick={onCompare}>
        See full comparison
        <svg width="18" height="18" viewBox="0 0 18 18"><path d="M3 9h11M14 9l-4-4M14 9l-4 4" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/></svg>
      </button>
    </div>
  );
}

function Stat({ label, value, sub }){
  return (
    <div className="stat">
      <div className="stat-label">{label}</div>
      <div className="stat-value">{value}</div>
      {sub && <div className="stat-sub">{sub}</div>}
    </div>
  );
}

function ResultScreen({ origin, dest, salary, offer, onBack }){
  const r = compare(origin, dest, salary, offer);
  const cheaper = r.costDeltaPct < 0;
  const powerUp = r.powerPct >= 0;
  return (
    <div className="screen result">
      <header className="top result-top">
        <button className="icon-btn back" onClick={onBack} aria-label="Back">
          <svg width="11" height="18" viewBox="0 0 11 18"><path d="M9 2L2 9l7 7" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round"/></svg>
        </button>
        <div className="route-mini">
          <RegionChip cc={origin.cc} size={22} /> {origin.city}
          <span className="rm-arrow">→</span>
          <RegionChip cc={dest.cc} size={22} /> {dest.city}
        </div>
      </header>

      <h2 className="verdict">
        {dest.city} is{' '}
        <em className={cheaper?'down':'up'}>{Math.abs(Math.round(r.costDeltaPct))}% {cheaper?'cheaper':'more expensive'}</em>{' '}
        than {origin.city}.
      </h2>

      <div className="card hero-card">
        <div className="hc-tag">BREAKEVEN SALARY</div>
        <div className="hc-amount">{money(r.breakevenLocal, dest)}<span className="hc-per">/mo</span></div>
        <div className="hc-desc">
          earn this in {dest.city} to keep the lifestyle your{' '}
          <strong>{money(salary, origin)}</strong> buys in {origin.city}.
        </div>
        <div className="hc-foot">
          <div>
            <div className="hcf-k">Keep your salary</div>
            <div className="hcf-v">{money(r.sameSalaryLocal, dest)}<span>/mo</span></div>
          </div>
          <div className={'hcf-pill '+(powerUp?'up':'down')}>
            {powerUp?'+':''}{Math.round(r.powerPct)}% buying power
          </div>
        </div>
      </div>

      {r.offer && (
        <div className="card offer-card">
          <div className="card-head">
            <span>Your offer</span>
            <span className={'card-badge '+(r.offer.vsBreakevenPct>=0?'down':'up')}>
              {r.offer.vsBreakevenPct>=0?'+':'−'}{Math.abs(Math.round(r.offer.vsBreakevenPct))}% vs breakeven
            </span>
          </div>
          <div className="oc-amount">{money(offer, dest)}<span>/mo</span></div>
          <div className="oc-desc">
            Worth <strong>{money(r.offer.equivalentHomeLocal, origin)}</strong> in {origin.city} terms —
            your lifestyle would {r.offer.vsBreakevenPct>=0?'improve':'decline'} by about{' '}
            <strong>{Math.abs(Math.round(r.offer.vsBreakevenPct))}%</strong>.
          </div>
        </div>
      )}

      <div className="card">
        <div className="card-head">
          <span>Purchasing power</span>
          <span className={'card-badge '+(powerUp?'up':'down')}>{powerUp?'goes further':'goes less far'}</span>
        </div>
        <div className="power-headline">
          Your money goes <strong className={powerUp?'up':'down'}>{powerUp?'+':''}{Math.round(r.powerPct)}%</strong> {powerUp?'further':'less far'}
        </div>
        <PowerBar pct={r.powerPct} />
      </div>

      <div className="card">
        <div className="card-head">
          <span>Lifestyle level</span>
          {r.offer && <span className="legend"><i className="lg-ghost" />now <i className="lg-solid" />with offer</span>}
        </div>
        {(()=>{ const t = r.offer || r; return (
          <React.Fragment>
            <div className="tier-head">
              <span className="tier-name" style={{ color:`var(--tier-${t.tierIndex})` }}>{t.tier.label}</span>
              <span className="tier-blurb">{t.tier.blurb}</span>
            </div>
            <TierMeter tierIndex={t.tierIndex} pos={t.tierPos} ghostPos={r.offer ? r.homeTierPos : null} />
          </React.Fragment>
        ); })()}
      </div>

      <div className="card">
        <div className="card-head">
          <span>Cost breakdown</span>
          <span className="legend"><i className="lg-o" />{origin.city} <i className="lg-d" />{dest.city}</span>
        </div>
        <Breakdown origin={origin} dest={dest} />
      </div>

      <p className="foot-note">
        Indexed to New York = 100. Currency converted at 1 {origin.cur} ≈ {(dest.perUSD/origin.perUSD).toFixed(2)} {dest.cur}.
        Estimates for guidance only.
      </p>
    </div>
  );
}

function PickerSheet({ open, role, current, onClose, onSelect }){
  const [q, setQ] = useState('');
  useEffect(()=>{ if(open) setQ(''); }, [open]);
  const list = CITIES.filter(c =>
    (c.city+' '+c.country).toLowerCase().includes(q.toLowerCase()));
  return (
    <Sheet open={open} onClose={onClose} title={role==='origin'?'Where do you live now?':'Compare with…'}>
      <div className="search">
        <svg width="17" height="17" viewBox="0 0 17 17"><circle cx="7" cy="7" r="5.5" fill="none" stroke="currentColor" strokeWidth="1.7"/><path d="M11 11l4.5 4.5" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round"/></svg>
        <input value={q} onChange={e=>setQ(e.target.value)} placeholder="Search city or country" autoFocus={false} />
      </div>
      <div className="city-list">
        {list.map(c=>(
          <button key={c.id} className={'city-list-row'+(c.id===current?' sel':'')} onClick={()=>onSelect(c.id)}>
            <RegionChip cc={c.cc} />
            <div className="clr-text">
              <div className="clr-name">{c.city}</div>
              <div className="clr-country">{c.country}</div>
            </div>
            <div className="clr-index">{c.index}</div>
            {c.id===current && <svg className="clr-check" width="16" height="16" viewBox="0 0 16 16"><path d="M2 8l4 4 8-9" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round"/></svg>}
          </button>
        ))}
        {list.length===0 && <div className="city-empty">No cities match “{q}”.</div>}
      </div>
    </Sheet>
  );
}

function KeypadSheet({ open, city, title, value, onClose, onChange }){
  const handle = (k)=>{
    let s = String(value);
    if(k==='del') s = s.slice(0,-1) || '0';
    else if(k==='.'){ if(!s.includes('.')) s = s + '.'; }
    else { s = (s==='0' ? '' : s) + k; }
    // limit length
    if(s.replace('.','').length <= 9) onChange(s);
  };
  return (
    <Sheet open={open} onClose={onClose} title={title}>
      <div className="kp-display">
        <span className="kp-sym">{city.sym}</span>
        <span className="kp-num">{Number(value||0).toLocaleString('en-US')}</span>
        <span className="kp-cur">{city.cur}</span>
      </div>
      <Keypad onKey={handle} />
      <button className="kp-done" onClick={onClose}>Done</button>
    </Sheet>
  );
}

const TWEAK_DEFAULTS = /*EDITMODE-BEGIN*/{
  "accent": "#2E8B57",
  "displayFont": "Space Grotesk",
  "corners": "soft"
}/*EDITMODE-END*/;

function App(){
  const [t, setTweak] = useTweaks(TWEAK_DEFAULTS);
  useEffect(()=>{
    const root = document.documentElement.style;
    root.setProperty('--green', t.accent);
    root.setProperty('--green-soft', `color-mix(in oklch, ${t.accent} 14%, transparent)`);
    root.setProperty('--tier-2', t.accent);
    root.setProperty('--font-d', `'${t.displayFont}', system-ui, sans-serif`);
    root.setProperty('--r', t.corners==='sharp' ? '8px' : '22px');
  }, [t.accent, t.displayFont, t.corners]);

  const [originId, setOriginId] = useState('nyc');
  const [destId, setDestId]     = useState('lis');
  const [salaryStr, setSalaryStr] = useState('6500');
  const [offerStr, setOfferStr] = useState('');
  const [screen, setScreen] = useState('home');     // home | result
  const [picker, setPicker] = useState(null);        // null | origin | dest
  const [keypad, setKeypad] = useState(null);        // null | 'salary' | 'offer'

  const origin = CITY[originId], dest = CITY[destId];
  const salary = Number(salaryStr) || 0;
  const offer = Number(offerStr) || 0;

  const handlePick = (role) => setPicker(role);
  const selectCity = (id) => {
    if(picker==='origin'){ if(id===destId) setDestId(originId); setOriginId(id); }
    else { if(id===originId) setOriginId(destId); setDestId(id); }
    setPicker(null);
  };
  const swap = () => { setOriginId(destId); setDestId(originId); };

  return (
    <div className="app-root">
      <div className={'slider screen-'+screen}>
        <div className="panel">
          <HomeScreen origin={origin} dest={dest} salary={salary} offer={offer}
            onPick={handlePick} onSwap={swap}
            onEditSalary={()=>setKeypad('salary')}
            onEditOffer={()=>setKeypad('offer')}
            onClearOffer={()=>setOfferStr('')}
            onCompare={()=>{ document.querySelector('.panel.result-panel')?.scrollTo(0,0); setScreen('result'); }} />
        </div>
        <div className="panel result-panel">
          {screen==='result' &&
            <ResultScreen origin={origin} dest={dest} salary={salary} offer={offer} onBack={()=>setScreen('home')} />}
        </div>
      </div>

      <PickerSheet open={!!picker} role={picker} current={picker==='origin'?originId:destId}
        onClose={()=>setPicker(null)} onSelect={selectCity} />
      <KeypadSheet open={keypad!==null}
        city={keypad==='offer'?dest:origin}
        title={keypad==='offer'?`Salary offer in ${dest.city}`:'Your monthly salary'}
        value={keypad==='offer'?offerStr:salaryStr}
        onClose={()=>setKeypad(null)}
        onChange={keypad==='offer'?setOfferStr:setSalaryStr} />

      <TweaksPanel>
        <TweakSection label="Accent" />
        <TweakColor label="Positive color" value={t.accent}
          options={['#2E8B57','#1F9E8F','#3B6FE0','#C08A2E']}
          onChange={(v)=>setTweak('accent', v)} />
        <TweakSection label="Type" />
        <TweakRadio label="Display font" value={t.displayFont}
          options={['Space Grotesk','Archivo','Spectral']}
          onChange={(v)=>setTweak('displayFont', v)} />
        <TweakSection label="Shape" />
        <TweakRadio label="Card corners" value={t.corners}
          options={['soft','sharp']}
          onChange={(v)=>setTweak('corners', v)} />
      </TweaksPanel>
    </div>
  );
}

function Root(){
  return (
    <div className="stage">
      <IOSDevice>
        <App />
      </IOSDevice>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<Root />);

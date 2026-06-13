// Kam — reusable visual components (attach to window for cross-file use)

// 2-letter region chip used in place of flags
function RegionChip({ cc, size=28 }){
  return (
    <div className="region-chip" style={{ width:size, height:size, fontSize:size*0.38 }}>{cc}</div>
  );
}

// Diverging purchasing-power bar. pct>0 = money goes further (green).
function PowerBar({ pct }){
  const clamped = Math.max(-80, Math.min(160, pct));
  // map -80..160 onto 0..100 with 0% sitting where value 0 lands
  const span = 160 - (-80);
  const zero = (0 - (-80)) / span * 100;      // baseline position
  const val  = (clamped - (-80)) / span * 100;
  const up = pct >= 0;
  const left = up ? zero : val;
  const width = Math.abs(val - zero);
  return (
    <div className="powerbar">
      <div className="powerbar-track">
        <div className="powerbar-fill" style={{
          left:`${left}%`, width:`${width}%`,
          background: up ? 'var(--green)' : 'var(--terra)',
        }} />
        <div className="powerbar-zero" style={{ left:`${zero}%` }} />
      </div>
      <div className="powerbar-labels">
        <span>Less far</span><span>Same</span><span>Further</span>
      </div>
    </div>
  );
}

// Four-segment lifestyle tier meter with a marker dot
function TierMeter({ tierIndex, pos, ghostPos }){
  return (
    <div className="tiermeter">
      <div className="tiermeter-track">
        {TIERS.map((t,i)=>(
          <div key={t.key}
            className={'tier-seg'+(i===tierIndex?' active':'')}
            style={{ '--tc':`var(--tier-${i})` }}>
            <span>{t.label}</span>
          </div>
        ))}
        {ghostPos!=null && <div className="tier-marker ghost" style={{ left:`calc(${ghostPos*100}% )` }} />}
        <div className="tier-marker" style={{ left:`calc(${pos*100}% )` }} />
      </div>
    </div>
  );
}

// Category breakdown: origin vs dest bars
function Breakdown({ origin, dest }){
  const max = Math.max(...CATS.map(c=>Math.max(catIndex(origin,c),catIndex(dest,c))));
  return (
    <div className="breakdown">
      {CATS.map(cat=>{
        const o = catIndex(origin,cat), d = catIndex(dest,cat);
        const diff = (d-o)/o*100;
        const cheaper = d < o;
        return (
          <div className="bd-row" key={cat.key}>
            <div className="bd-label">{cat.label}</div>
            <div className="bd-bars">
              <div className="bd-bar bd-origin" style={{ width:`${o/max*100}%` }} />
              <div className="bd-bar bd-dest" style={{ width:`${d/max*100}%`,
                background: cheaper ? 'var(--green)' : 'var(--terra)' }} />
            </div>
            <div className={'bd-diff '+(cheaper?'down':'up')}>
              {cheaper?'−':'+'}{Math.abs(Math.round(diff))}%
            </div>
          </div>
        );
      })}
    </div>
  );
}

// Custom number pad
function Keypad({ onKey }){
  const keys = ['1','2','3','4','5','6','7','8','9','.','0','del'];
  return (
    <div className="keypad">
      {keys.map(k=>(
        <button key={k} className={'kp-key'+(k==='del'?' kp-del':'')}
          onClick={()=>onKey(k)}>
          {k==='del'
            ? <svg width="26" height="20" viewBox="0 0 26 20"><path d="M8 2h15a1.5 1.5 0 011.5 1.5v13A1.5 1.5 0 0123 18H8L1 10 8 2z" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinejoin="round"/><path d="M12 7l7 6M19 7l-7 6" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round"/></svg>
            : k}
        </button>
      ))}
    </div>
  );
}

// Bottom sheet shell
function Sheet({ open, onClose, children, title }){
  return (
    <div className={'sheet-wrap'+(open?' open':'')}>
      <div className="sheet-scrim" onClick={onClose} />
      <div className="sheet">
        <div className="sheet-grip" />
        {title && <div className="sheet-title">{title}</div>}
        {children}
      </div>
    </div>
  );
}

Object.assign(window, { RegionChip, PowerBar, TierMeter, Breakdown, Keypad, Sheet });

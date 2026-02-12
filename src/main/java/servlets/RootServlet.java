package servlets;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet handling the root endpoint.
 */
public class RootServlet extends HttpServlet {
    @Override
 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        response.getWriter().println("""
<!doctype html>
<html>
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width,initial-scale=1" />
<title>CI Builds</title>
<style>
    body { font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial; margin: 0; background:#0b1020; color:#e9ecf1; }
    header { padding: 16px 20px; border-bottom: 1px solid #1f2a44; position: sticky; top: 0; background:#0b1020; }
    .wrap { display: grid; grid-template-columns: 1.2fr 1fr; gap: 14px; padding: 14px 20px 20px; }
    .card { background:#0f1830; border:1px solid #1f2a44; border-radius: 12px; overflow:hidden; }
    .card h2 { margin:0; padding:12px 14px; font-size: 13px; color:#b7c2d8; border-bottom:1px solid #1f2a44; }
    table { width:100%; border-collapse: collapse; }
    th, td { padding: 10px 12px; border-bottom: 1px solid #1f2a44; font-size: 13px; vertical-align: top; }
    th { text-align: left; color:#b7c2d8; font-weight: 600; }
    tr:hover { background:#121f3f; cursor:pointer; }
    .badge { display:inline-block; padding: 3px 10px; border-radius: 999px; font-size: 12px; border:1px solid #2b3b60; }
    .ok { background: rgba(16,185,129,.12); border-color: rgba(16,185,129,.35); }
    .bad { background: rgba(239,68,68,.12); border-color: rgba(239,68,68,.35); }
    .mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace; }
    pre { margin: 0; padding: 12px 14px; max-height: 70vh; overflow: auto; white-space: pre-wrap; word-break: break-word; }
    .muted { color:#98a6c2; }
    button { background:#18284f; color:#e9ecf1; border:1px solid #2b3b60; border-radius:10px; padding:8px 10px; font-size:13px; cursor:pointer; }
    button:hover { background:#1b2f5e; }
</style>
</head>
<body>
<header style="display:flex; gap:10px; align-items:center;">
<div style="font-weight:700;">CI Builds</div>
<div class="muted" id="count">Loading…</div>
<div style="flex:1;"></div>
<button id="refresh">Refresh</button>
</header>

<div class="wrap">
<section class="card">
    <h2>Builds</h2>
    <div style="overflow:auto;">
    <table>
        <thead>
        <tr>
            <th style="width:90px;">ID</th>
            <th style="width:120px;">Status</th>
            <th style="width:220px;">Date</th>
            <th style="width:160px;">Commit</th>
        </tr>
        </thead>
        <tbody id="rows"></tbody>
    </table>
    </div>
</section>

<section class="card">
    <h2>Details</h2>
    <div style="padding:10px 14px; border-bottom:1px solid #1f2a44;">
    <div class="muted" id="meta">Click a build.</div>
    </div>
    <pre class="mono" id="output"></pre>
</section>
</div>

<script>
const rowsEl = document.getElementById('rows');
const metaEl = document.getElementById('meta');
const outputEl = document.getElementById('output');
const countEl = document.getElementById('count');

function esc(s){ return (s||'').replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('>','&gt;'); }

async function loadBuildList() {
rowsEl.innerHTML = '<tr><td colspan="4" class="muted">Loading…</td></tr>';

// your API returns { builds: [1,2,3] } today
const res = await fetch('/builds');
const data = await res.json();
const ids = data.builds || [];

countEl.textContent = ids.length + ' builds';

// Fetch details for the newest ~30 to avoid spamming
const idsToShow = ids.slice(-30).reverse();
const details = await Promise.all(idsToShow.map(id => fetch('/builds/' + id).then(r => r.json())));

rowsEl.innerHTML = details.map(b => {
    const badgeClass = b.success ? 'badge ok' : 'badge bad';
    return `
    <tr data-id="${b.url.split('/').pop()}">
        <td class="mono">#${esc(b.url.split('/').pop())}</td>
        <td><span class="${badgeClass}">${b.success ? 'SUCCESS' : 'FAILURE'}</span></td>
        <td class="muted">${esc(b.date)}</td>
        <td class="mono">${esc((b.commitSHA||'').slice(0,7))}</td>
    </tr>
    `;
}).join('');

for (const tr of rowsEl.querySelectorAll('tr[data-id]')) {
    tr.addEventListener('click', () => loadBuild(tr.dataset.id));
}

// Auto-select latest
if (idsToShow.length) loadBuild(idsToShow[0]);
}

async function loadBuild(id) {
metaEl.textContent = 'Loading #' + id + '…';
outputEl.textContent = '';

const res = await fetch('/builds/' + id);
const b = await res.json();

metaEl.innerHTML =
    `<span class="mono">#${esc(id)}</span> • ` +
    `<span class="${b.success ? 'badge ok' : 'badge bad'}">${b.success ? 'SUCCESS' : 'FAILURE'}</span> • ` +
    `<span class="muted">${esc(b.date)}</span> • ` +
    `<span class="mono">${esc((b.commitSHA||'').slice(0,12))}</span>`;

outputEl.textContent = b.buildOutput || '';
}

document.getElementById('refresh').addEventListener('click', loadBuildList);
loadBuildList();
</script>
</body>
</html>
        """);
    }
}
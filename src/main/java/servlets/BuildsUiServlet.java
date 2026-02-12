package servlets;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BuildsUiServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html; charset=utf-8");
    response.setStatus(HttpServletResponse.SC_OK);

    response.getWriter().println("""
<!doctype html>
<html>
<head>
<meta charset="utf-8" />
<title>CI Builds</title>
<style>
body { font-family: system-ui; margin:0; background:#0b1020; color:#e9ecf1; }
header { padding:16px; border-bottom:1px solid #1f2a44; }
.wrap { display:grid; grid-template-columns:1fr 1.3fr; gap:14px; padding:14px; }
.card { background:#0f1830; border:1px solid #1f2a44; border-radius:12px; overflow:hidden; }
.card h2 { margin:0; padding:12px; font-size:13px; border-bottom:1px solid #1f2a44; }
table { width:100%; border-collapse:collapse; }
th, td { padding:8px 10px; border-bottom:1px solid #1f2a44; font-size:13px; }
tr:hover { background:#121f3f; cursor:pointer; }
pre { margin:0; padding:12px; max-height:70vh; overflow:auto; white-space:pre-wrap; }
.badge { padding:3px 8px; border-radius:999px; font-size:12px; }
.ok { background:#143f2e; }
.bad { background:#4a1f1f; }
</style>
</head>
<body>

<header>
  <strong>CI Build History</strong>
</header>

<div class="wrap">
  <section class="card">
    <h2>Builds</h2>
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>Status</th>
          <th>Date</th>
        </tr>
      </thead>
      <tbody id="rows"></tbody>
    </table>
  </section>

  <section class="card">
    <h2>Details</h2>
    <pre id="output">Click a build.</pre>
  </section>
</div>

<script>
const rows = document.getElementById("rows");
const output = document.getElementById("output");

async function loadBuilds() {
  const res = await fetch("/builds");
  if (!res.ok) {
    output.textContent = "Failed to load /builds: " + res.status;
    return;
  }

  const data = await res.json();
  const builds = data.builds || [];

  rows.innerHTML = "";

  for (const b of builds.slice().reverse()) {
    const res2 = await fetch(b.url); // <-- use the url field
    if (!res2.ok) continue;

    const build = await res2.json();
    const id = b.id;

    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>#${id}</td>
      <td><span class="badge ${build.success ? "ok" : "bad"}">
        ${build.success ? "SUCCESS" : "FAILURE"}
      </span></td>
      <td>${build.date ?? ""}</td>
    `;
    tr.onclick = () => {
      output.textContent = build.buildOutput ?? "";
    };
    rows.appendChild(tr);
  }

  if (builds.length === 0) {
    output.textContent = "No builds found in DB.";
  }
}

loadBuilds();
</script>

</body>
</html>
        """);
    }
}

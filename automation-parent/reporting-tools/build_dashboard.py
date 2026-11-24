#!/usr/bin/env python3
"""Generate an interactive GitHub Pages dashboard with real test-type filters."""
import html
import json
import pathlib
import sys

results = pathlib.Path(sys.argv[1])
output = pathlib.Path(sys.argv[2])
rows = []
for path in sorted(results.glob("*-result.json")):
    result = json.loads(path.read_text(encoding="utf-8"))
    if result.get("status") == "skipped":
        continue
    tags = {x.get("value") for x in result.get("labels", []) if x.get("name") == "tag"}
    category = next((x for x in ("Playwright", "Selenium", "API", "Unit") if x in tags), "Unit")
    rows.append({"name": result.get("name", "Unnamed test"), "type": category,
                 "family": "UI" if category in ("Playwright", "Selenium") else category,
                 "status": result.get("status", "unknown"),
                 "id": result.get("uuid", "")})
data = json.dumps(rows, ensure_ascii=False).replace("<", "\\u003c").replace(">", "\\u003e")
page = """<!doctype html><html lang="en"><head><meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>Automation Test Reports</title>
<style>
body{font:15px system-ui,sans-serif;background:#f6f8fb;color:#1c293b;margin:0}
main{max-width:1100px;margin:40px auto;padding:0 20px}h1{margin-bottom:5px}
p{color:#536276}nav{display:flex;gap:10px;flex-wrap:wrap;margin:20px 0}
nav a{padding:10px 16px;background:#193b6b;color:white;text-decoration:none;border-radius:8px}
.filters{display:flex;gap:8px;flex-wrap:wrap;margin:20px 0}
button{padding:10px 15px;border:1px solid #cbd5e1;border-radius:9px;background:white;cursor:pointer}
button.active{background:#193b6b;color:white;border-color:#193b6b}
input{padding:11px;border:1px solid #cbd5e1;border-radius:8px;width:min(100%,340px)}
table{border-collapse:collapse;width:100%;background:white;border-radius:12px;overflow:hidden}
th,td{padding:12px;text-align:left;border-bottom:1px solid #e7ecf2}
th{background:#eaf0f8}td a{color:#174b8d}.passed{color:#177346}.failed,.broken{color:#b91c1c}
</style></head><body><main><h1>Automation Test Reports</h1>
<p>Filter actual CI test results by category and search by test name.</p>
<nav><a href="allure/index.html">Allure — steps and evidence</a><a href="extent/index.html">Extent — categories and steps</a></nav>
<div class="filters" id="filters"></div>
<input id="search" placeholder="Search test names..." aria-label="Search tests">
<p id="count" aria-live="polite"></p>
<table><thead><tr><th>Test</th><th>Type</th><th>Status</th><th>Details</th></tr></thead><tbody id="tests"></tbody></table>
<script>
const tests=__DATA__;
let selected='All';
const filterNames=['All','UI','API','Selenium','Playwright','Unit'];
const controls=document.getElementById('filters');
const search=document.getElementById('search');
const tbody=document.getElementById('tests');
function render(){
  controls.replaceChildren();
  for(const category of filterNames){
    const b=document.createElement('button');b.textContent=category;b.className=selected===category?'active':'';
    b.setAttribute('aria-pressed',String(selected===category));
    b.onclick=()=>{selected=category;render()};controls.append(b);
  }
  const query=search.value.trim().toLowerCase();
  const visible=tests.filter(t=>(selected==='All'||t.type===selected||t.family===selected)&&t.name.toLowerCase().includes(query));
  document.getElementById('count').textContent=visible.length+' of '+tests.length+' executed tests';
  tbody.replaceChildren();
  for(const t of visible){
    const tr=document.createElement('tr');
    for(const val of [t.name,t.type,t.status]){
      const td=document.createElement('td');td.textContent=val;
      if(val===t.status)td.className=t.status;tr.append(td);
    }
    const td=document.createElement('td');const a=document.createElement('a');
    a.href='allure/index.html#testresult/'+encodeURIComponent(t.id);
    a.textContent='View steps';td.append(a);tr.append(td);tbody.append(tr);
  }
}
search.oninput=render;render();
</script></main></body></html>"""
output.parent.mkdir(parents=True, exist_ok=True)
output.write_text(page.replace("__DATA__", data), encoding="utf-8")
print(f"Dashboard generated: {len(rows)} tests, {output}")

const pptxgen = require("pptxgenjs");

const TEXT_DARK        = "241B18";
const TEXT_MUTED       = "6E5D57";
const ACCENT           = "FF6B45";
const ACCENT_DEEP      = "D6472A";
const TEXT_LIGHT       = "FBF3EF";
const TEXT_LIGHT_MUTED = "D8BEB4";
const GLASS_LINE_LIGHT = "FFD9C7";
const GLASS_LINE_DARK  = "FF9670";
const DIVIDER          = "ECDED8";

const FONT_HEAD = "Poppins";
const FONT_BODY = "Poppins";

const ICON_META = require("./icons/meta.json");
const ICON = (name, color) => `icons/${name}_${color}.png`;

const pres = new pptxgen();
pres.layout = "LAYOUT_WIDE";

function freshShadow(color = "1B3A5C", opacity = 0.16, blur = 12, offset = 4, angle = 90) {
  return { type: "outer", color, opacity, blur, offset, angle };
}

function bgSlide(dark = false) {
  const s = pres.addSlide();
  s.background = { path: dark ? "bg_dark.png" : "bg_light.png" };
  return s;
}

function glassRect(slide, { x, y, w, h, r = 0.12, dark = false, fillOpacity = 62, borderOpacity = 55 }) {
  slide.addShape(pres.ShapeType.roundRect, {
    x, y, w, h, rectRadius: r,
    fill: { color: dark ? "3A2A22" : "FFFFFF", transparency: 100 - fillOpacity },
    line: { color: dark ? GLASS_LINE_DARK : GLASS_LINE_LIGHT, width: 1, transparency: 100 - borderOpacity },
    shadow: freshShadow(dark ? "020810" : "1B3A5C", dark ? 0.35 : 0.14, 14, 5),
  });
}

function iconImg(slide, { x, y, box, icon, color }) {
  const aspect = (ICON_META[icon] && ICON_META[icon].aspect) || 1;
  let iw, ih;
  if (aspect >= 1) { iw = box; ih = box / aspect; } else { ih = box; iw = box * aspect; }
  slide.addImage({ path: ICON(icon, color), x: x + (box - iw) / 2, y: y + (box - ih) / 2, w: iw, h: ih });
}

function wordmark(slide, { x, y, size = 16, dark = false }) {
  slide.addText(
    [
      { text: "Tech", options: { color: ACCENT, bold: true } },
      { text: "Hub", options: { color: dark ? TEXT_LIGHT : TEXT_DARK, bold: true } },
    ],
    { x, y, w: 2.2, h: size / 40 + 0.3, fontFace: FONT_HEAD, fontSize: size, isTextBox: true }
  );
}

function runningHeader(slide, label, dark = false) {
  wordmark(slide, { x: 0.7, y: 0.35, size: 13, dark });
  slide.addText(label, {
    x: 8.3, y: 0.38, w: 4.3, h: 0.3, align: "right", fontFace: FONT_BODY, fontSize: 10, bold: true,
    color: dark ? TEXT_LIGHT_MUTED : TEXT_MUTED, isTextBox: true, charSpacing: 1,
  });
}

// =====================================================================
// SLIDE 1 — Title  (hero image + team credits grafted on afterward)
// =====================================================================
{
  const s = bgSlide(true);
  const icons = ["smartphone", "tablet", "laptop", "pc", "watch", "headphones", "tv"];
  const rowW = 7.4, startX = (13.333 - rowW) / 2, iy = 6.55, isz = 0.42;
  icons.forEach((name, i) => {
    const gap = rowW / (icons.length - 1);
    iconImg(s, { x: startX + gap * i - isz / 2, y: iy, box: isz, icon: name, color: "8A6A62" });
  });

  s.addText("STARTUP HACKATHON  ·  TEAM-RISE", {
    x: 0.9, y: 0.75, w: 8, h: 0.35, fontFace: FONT_BODY, fontSize: 12, bold: true, color: ACCENT, isTextBox: true, charSpacing: 2,
  });
  s.addText(
    [{ text: "Tech", options: { color: ACCENT } }, { text: "Hub", options: { color: TEXT_LIGHT } }],
    { x: 0.85, y: 2.15, w: 10, h: 1.3, fontFace: FONT_HEAD, fontSize: 62, bold: true, isTextBox: true }
  );
  s.addText("A simple way to choose the right technology.", {
    x: 0.9, y: 3.35, w: 9.5, h: 0.55, fontFace: FONT_HEAD, fontSize: 21, color: TEXT_LIGHT_MUTED, isTextBox: true,
  });
  s.addText("TechHub helps people compare, understand, and buy smartphones and tablets with confidence.", {
    x: 0.9, y: 3.95, w: 8, h: 0.6, fontFace: FONT_BODY, fontSize: 13, color: TEXT_LIGHT_MUTED, isTextBox: true, lineSpacingMultiple: 1.3,
  });
  // (hero illustration + "Presented by" team credit are grafted into this space afterward)
}

// =====================================================================
// SLIDE 2 — The Problem
// =====================================================================
{
  const s = bgSlide(false);
  runningHeader(s, "THE PROBLEM");
  s.addText("A CLEARER WAY TO CHOOSE TECH", { x: 0.9, y: 1.0, w: 8, h: 0.35, fontFace: FONT_BODY, fontSize: 11, bold: true, color: ACCENT, isTextBox: true, charSpacing: 2 });
  s.addText("Buying technology is confusing.", {
    x: 0.85, y: 1.35, w: 7.2, h: 1.3, fontFace: FONT_HEAD, fontSize: 34, bold: true, color: TEXT_DARK, isTextBox: true, lineSpacingMultiple: 1.08,
  });

  const points = [
    { title: "Too many places to check", body: "Specs on one site, reviews on another, opinions scattered across videos." },
    { title: "Reviews often disagree", body: "One review says \"great,\" another says \"skip it.\" Hard to know who to trust." },
    { title: "Still an unclear choice", body: "After all that research, people may still pick the wrong product." },
  ];
  let ly = 2.85;
  points.forEach((p, i) => {
    s.addShape(pres.ShapeType.line, { x: 0.9, y: ly, w: 6.6, h: 0, line: { color: DIVIDER, width: 1 } });
    s.addText(String(i + 1).padStart(2, "0"), { x: 0.9, y: ly + 0.12, w: 0.6, h: 0.5, fontFace: FONT_HEAD, fontSize: 15, bold: true, color: ACCENT, isTextBox: true });
    s.addText(p.title, { x: 1.55, y: ly + 0.1, w: 2.7, h: 0.5, fontFace: FONT_HEAD, fontSize: 14, bold: true, color: TEXT_DARK, isTextBox: true });
    s.addText(p.body, { x: 4.2, y: ly + 0.1, w: 3.3, h: 0.85, fontFace: FONT_BODY, fontSize: 10.5, color: TEXT_MUTED, isTextBox: true, lineSpacingMultiple: 1.2 });
    ly += 1.05;
  });

  glassRect(s, { x: 8.15, y: 1.35, w: 4.45, h: 4.85, r: 0.16 });
  s.addText("THE RESULT", { x: 8.55, y: 1.75, w: 3.7, h: 0.3, fontFace: FONT_BODY, fontSize: 11, bold: true, color: ACCENT, isTextBox: true, charSpacing: 2 });
  s.addText("Too much research.\nStill the wrong choice.", {
    x: 8.55, y: 2.2, w: 3.75, h: 1.7, fontFace: FONT_HEAD, fontSize: 24, bold: true, color: TEXT_DARK, isTextBox: true, lineSpacingMultiple: 1.1,
  });
  s.addShape(pres.ShapeType.line, { x: 8.55, y: 3.85, w: 1.0, h: 0, line: { color: ACCENT, width: 2.5 } });
  s.addText("TechHub brings everything into one place — with one clear, simple answer.", {
    x: 8.55, y: 4.1, w: 3.75, h: 1.7, fontFace: FONT_BODY, fontSize: 12.5, italic: true, color: TEXT_MUTED, isTextBox: true, lineSpacingMultiple: 1.3,
  });
}

// =====================================================================
// SLIDE 3 — Who Will Use TechHub
// =====================================================================
{
  const s = bgSlide(false);
  runningHeader(s, "TARGET USERS");
  s.addText("WHO IT'S FOR", { x: 0.9, y: 0.95, w: 6, h: 0.3, fontFace: FONT_BODY, fontSize: 11, bold: true, color: ACCENT, isTextBox: true, charSpacing: 2 });
  s.addText("Made for people who just want a simple answer.", {
    x: 0.85, y: 1.28, w: 11, h: 0.75, fontFace: FONT_HEAD, fontSize: 28, bold: true, color: TEXT_DARK, isTextBox: true,
  });

  const users = [
    { icon: "graduationCap", title: "Students", body: "Buying their first phone or tablet on a tight budget." },
    { icon: "users", title: "First-time buyers", body: "No one around to ask for honest advice." },
    { icon: "wallet", title: "Budget-conscious shoppers", body: "Want the best value, not just the cheapest option." },
    { icon: "gift", title: "Parents", body: "Buying a phone or tablet for their kids." },
    { icon: "bullseye", title: "Anyone who wants it simple", body: "Not a tech expert, and doesn't want to become one." },
  ];
  const cw = 2.2, gap = 0.22, startX = 0.7, cy = 2.55, ch = 3.2;
  users.forEach((u, i) => {
    const cx = startX + i * (cw + gap);
    glassRect(s, { x: cx, y: cy, w: cw, h: ch, r: 0.14, fillOpacity: 55 });
    iconImg(s, { x: cx + (cw - 0.5) / 2, y: cy + 0.3, box: 0.5, icon: u.icon, color: ACCENT });
    s.addText(u.title, { x: cx + 0.15, y: cy + 1.05, w: cw - 0.3, h: 0.6, align: "center", fontFace: FONT_HEAD, fontSize: 12.5, bold: true, color: TEXT_DARK, isTextBox: true });
    s.addText(u.body, { x: cx + 0.18, y: cy + 1.65, w: cw - 0.36, h: 1.4, align: "center", fontFace: FONT_BODY, fontSize: 9.5, color: TEXT_MUTED, isTextBox: true, lineSpacingMultiple: 1.25 });
  });
}

// =====================================================================
// SLIDE 4 — Our Solution
// =====================================================================
{
  const s = bgSlide(false);
  runningHeader(s, "OUR SOLUTION");
  s.addText("OUR MAIN IDEA", { x: 0.9, y: 0.95, w: 6, h: 0.3, fontFace: FONT_BODY, fontSize: 11, bold: true, color: ACCENT, isTextBox: true, charSpacing: 2 });
  s.addText("One platform. One clear answer.", {
    x: 0.85, y: 1.28, w: 10, h: 0.75, fontFace: FONT_HEAD, fontSize: 32, bold: true, color: TEXT_DARK, isTextBox: true,
  });
  s.addText("TechHub won't just show more information — it organises everything and helps you decide.", {
    x: 0.9, y: 2.05, w: 8.5, h: 0.5, fontFace: FONT_BODY, fontSize: 12.5, color: TEXT_MUTED, isTextBox: true, lineSpacingMultiple: 1.3,
  });

  glassRect(s, { x: 0.7, y: 2.95, w: 11.9, h: 3.6, r: 0.16 });
  const stages = [
    { icon: "search", title: "Collect", body: "Gather details from trusted reviews and creators." },
    { icon: "star", title: "Score", body: "Give every product one simple, clear score." },
    { icon: "filter", title: "Filter", body: "Match products to your budget and needs." },
    { icon: "compare", title: "Compare & Suggest", body: "See products side-by-side and get a suggestion." },
  ];
  const innerX = 1.15, innerW = 11.1, stageW = innerW / stages.length;
  stages.forEach((st, i) => {
    const cx = innerX + i * stageW;
    if (i > 0) s.addShape(pres.ShapeType.line, { x: cx, y: 3.35, w: 0, h: 2.75, line: { color: DIVIDER, width: 1 } });
    s.addShape(pres.ShapeType.ellipse, { x: cx + stageW / 2 - 0.28, y: 3.45, w: 0.56, h: 0.56, fill: { color: ACCENT }, line: { type: "none" } });
    s.addText(String(i + 1), { x: cx + stageW / 2 - 0.28, y: 3.45, w: 0.56, h: 0.56, align: "center", valign: "middle", fontFace: FONT_HEAD, fontSize: 15, bold: true, color: "FFFFFF", isTextBox: true });
    s.addText(st.title, { x: cx + 0.1, y: 4.25, w: stageW - 0.2, h: 0.4, align: "center", fontFace: FONT_HEAD, fontSize: 14.5, bold: true, color: TEXT_DARK, isTextBox: true });
    s.addText(st.body, { x: cx + 0.22, y: 4.7, w: stageW - 0.44, h: 1.3, align: "center", fontFace: FONT_BODY, fontSize: 10, color: TEXT_MUTED, isTextBox: true, lineSpacingMultiple: 1.25 });
  });
}

// =====================================================================
// SLIDE 5 — How TechHub Works
// =====================================================================
{
  const s = bgSlide(false);
  runningHeader(s, "HOW IT WORKS");
  s.addText("HOW TECHHUB WORKS", { x: 0.9, y: 0.95, w: 6, h: 0.3, fontFace: FONT_BODY, fontSize: 11, bold: true, color: ACCENT, isTextBox: true, charSpacing: 2 });
  s.addText("Select  →  Filter  →  Compare  →  Decide", {
    x: 0.85, y: 1.28, w: 11, h: 0.75, fontFace: FONT_HEAD, fontSize: 27, bold: true, color: TEXT_DARK, isTextBox: true,
  });

  const steps = ["Select", "Filter", "Compare", "Decide"];
  const n = steps.length, trackY = 2.85, startX = 2.0, endX = 11.3;
  s.addShape(pres.ShapeType.line, { x: startX + 0.35, y: trackY, w: endX - startX - 0.7, h: 0, line: { color: DIVIDER, width: 2, dashType: "dash" } });
  steps.forEach((label, i) => {
    const cx = startX + (i * (endX - startX)) / (n - 1);
    const last = i === n - 1;
    s.addShape(pres.ShapeType.ellipse, { x: cx - 0.34, y: trackY - 0.34, w: 0.68, h: 0.68, fill: { color: last ? ACCENT : "FFFFFF" }, line: { color: ACCENT, width: last ? 0 : 2 }, shadow: freshShadow("1B3A5C", 0.12, 8, 3) });
    s.addText(String(i + 1), { x: cx - 0.34, y: trackY - 0.34, w: 0.68, h: 0.68, align: "center", valign: "middle", fontFace: FONT_HEAD, fontSize: 15, bold: true, color: last ? "FFFFFF" : ACCENT, isTextBox: true });
    s.addText(label, { x: cx - 0.9, y: trackY + 0.45, w: 1.8, h: 0.4, align: "center", fontFace: FONT_HEAD, fontSize: 14.5, bold: true, color: TEXT_DARK, isTextBox: true });
  });

  const detail = [
    "Choose a product category.",
    "Enter your budget and what matters to you.",
    "View suitable products and compare their scores.",
    "Pick the best one — or check the price to buy.",
  ];
  glassRect(s, { x: 1.0, y: 4.05, w: 6.6, h: 2.55, r: 0.14 });
  s.addText(detail.join("\n"), {
    x: 1.3, y: 4.25, w: 6.0, h: 1.7, fontFace: FONT_BODY, fontSize: 11.5, color: TEXT_MUTED, isTextBox: true, lineSpacingMultiple: 1.35, bullet: { code: "2022" },
  });

  glassRect(s, { x: 7.9, y: 4.05, w: 4.7, h: 2.55, r: 0.14, fillOpacity: 80, borderOpacity: 70 });
  iconImg(s, { x: 8.15, y: 4.3, box: 0.35, icon: "quote", color: ACCENT });
  s.addText("For example, a user can just say:", { x: 8.15, y: 4.78, w: 4.2, h: 0.3, fontFace: FONT_BODY, fontSize: 9.5, bold: true, color: TEXT_MUTED, isTextBox: true });
  s.addText("\u201cI want a phone under \u20b920,000 with a good camera and strong battery.\u201d", {
    x: 8.15, y: 5.08, w: 4.2, h: 1.35, fontFace: FONT_HEAD, fontSize: 13.5, italic: true, bold: true, color: TEXT_DARK, isTextBox: true, lineSpacingMultiple: 1.2,
  });
}

// =====================================================================
// SLIDE 6 — Product Score
// =====================================================================
{
  const s = bgSlide(false);
  runningHeader(s, "PRODUCT SCORE");
  s.addText("PRODUCT SCORE", { x: 0.9, y: 0.95, w: 6, h: 0.3, fontFace: FONT_BODY, fontSize: 11, bold: true, color: ACCENT, isTextBox: true, charSpacing: 2 });
  s.addText("A simple score for every product.", {
    x: 0.85, y: 1.28, w: 10, h: 0.75, fontFace: FONT_HEAD, fontSize: 30, bold: true, color: TEXT_DARK, isTextBox: true,
  });
  s.addText("Every product is scored on the things that matter most — here's an example:", {
    x: 0.9, y: 2.05, w: 8.5, h: 0.4, fontFace: FONT_BODY, fontSize: 12.5, color: TEXT_MUTED, isTextBox: true,
  });

  glassRect(s, { x: 0.7, y: 2.6, w: 7.6, h: 3.95, r: 0.16 });
  const scores = [
    ["Performance", 8.5], ["Camera", 8.0], ["Battery", 9.0], ["Display", 8.2],
    ["Software", 8.3], ["Build Quality", 8.6], ["Value for Money", 8.7],
  ];
  let sy = 2.85;
  scores.forEach(([label, val]) => {
    s.addText(label, { x: 1.0, y: sy, w: 2.0, h: 0.4, valign: "middle", fontFace: FONT_BODY, fontSize: 11.5, bold: true, color: TEXT_DARK, isTextBox: true });
    s.addShape(pres.ShapeType.roundRect, { x: 3.1, y: sy + 0.09, w: 4.2, h: 0.22, rectRadius: 0.11, fill: { color: "F0E4DE" }, line: { type: "none" } });
    s.addShape(pres.ShapeType.roundRect, { x: 3.1, y: sy + 0.09, w: 4.2 * (val / 10), h: 0.22, rectRadius: 0.11, fill: { color: ACCENT }, line: { type: "none" } });
    s.addText(val.toFixed(1), { x: 7.4, y: sy, w: 0.7, h: 0.4, valign: "middle", fontFace: FONT_HEAD, fontSize: 11.5, bold: true, color: ACCENT_DEEP, isTextBox: true });
    sy += 0.5;
  });

  glassRect(s, { x: 8.55, y: 2.6, w: 4.05, h: 3.95, r: 0.16, fillOpacity: 80, borderOpacity: 70 });
  iconImg(s, { x: 8.9, y: 2.95, box: 0.5, icon: "shield", color: ACCENT });
  s.addText("OUR PROMISE", { x: 8.9, y: 3.55, w: 3.4, h: 0.3, fontFace: FONT_BODY, fontSize: 10.5, bold: true, color: ACCENT, isTextBox: true, charSpacing: 1.5 });
  s.addText("The score is always easy to understand.", {
    x: 8.9, y: 3.9, w: 3.4, h: 0.9, fontFace: FONT_HEAD, fontSize: 16.5, bold: true, color: TEXT_DARK, isTextBox: true, lineSpacingMultiple: 1.15,
  });
  s.addText("Users won't just see a number — they'll see why the product earned it. That's what makes the score trustworthy.", {
    x: 8.9, y: 4.85, w: 3.4, h: 1.5, fontFace: FONT_BODY, fontSize: 10.5, italic: true, color: TEXT_MUTED, isTextBox: true, lineSpacingMultiple: 1.3,
  });
}

// =====================================================================
// SLIDE 7 — Technology & What Makes Us Different
// =====================================================================
{
  const s = bgSlide(false);
  runningHeader(s, "TECHNOLOGY");
  s.addText("TECHNOLOGY & WHAT MAKES US DIFFERENT", { x: 0.9, y: 0.95, w: 9, h: 0.3, fontFace: FONT_BODY, fontSize: 11, bold: true, color: ACCENT, isTextBox: true, charSpacing: 1.5 });
  s.addText("Built simply. Designed to be trusted.", {
    x: 0.85, y: 1.28, w: 10.5, h: 0.75, fontFace: FONT_HEAD, fontSize: 28, bold: true, color: TEXT_DARK, isTextBox: true,
  });

  glassRect(s, { x: 0.7, y: 2.35, w: 5.75, h: 4.2, r: 0.16 });
  s.addText("HOW IT'S BUILT", { x: 1.0, y: 2.6, w: 5, h: 0.3, fontFace: FONT_BODY, fontSize: 11, bold: true, color: ACCENT, isTextBox: true, charSpacing: 1.5 });
  const tech = [
    { icon: "code", title: "Simple, clean design", body: "One design system used across every screen." },
    { icon: "database", title: "One shared database", body: "The same product data powers every category." },
    { icon: "brain", title: "One clear scoring formula", body: "Every product is scored the same fair way." },
  ];
  let ty = 3.15;
  tech.forEach((t) => {
    iconImg(s, { x: 1.0, y: ty, box: 0.42, icon: t.icon, color: ACCENT });
    s.addText(t.title, { x: 1.6, y: ty - 0.03, w: 4.6, h: 0.35, fontFace: FONT_HEAD, fontSize: 12.5, bold: true, color: TEXT_DARK, isTextBox: true });
    s.addText(t.body, { x: 1.6, y: ty + 0.32, w: 4.6, h: 0.5, fontFace: FONT_BODY, fontSize: 10, color: TEXT_MUTED, isTextBox: true, lineSpacingMultiple: 1.2 });
    ty += 0.95;
  });

  glassRect(s, { x: 6.7, y: 2.35, w: 5.9, h: 4.2, r: 0.16, dark: true, fillOpacity: 88, borderOpacity: 60 });
  s.addText("WHY WE'RE DIFFERENT", { x: 7.0, y: 2.6, w: 5.3, h: 0.3, fontFace: FONT_BODY, fontSize: 11, bold: true, color: ACCENT, isTextBox: true, charSpacing: 1.5 });
  const diffs = [
    "We don't just show more information — we give one clear answer.",
    "Every score is explained, not just a number on a page.",
    "Built as one connected system — not a pile of separate pages.",
  ];
  let dy = 3.2;
  diffs.forEach((d) => {
    s.addShape(pres.ShapeType.ellipse, { x: 7.0, y: dy + 0.09, w: 0.1, h: 0.1, fill: { color: ACCENT }, line: { type: "none" } });
    s.addText(d, { x: 7.25, y: dy - 0.08, w: 5.1, h: 0.7, fontFace: FONT_BODY, fontSize: 12, color: TEXT_DARK, isTextBox: true, lineSpacingMultiple: 1.25 });
    dy += 1.0;
  });
}

// =====================================================================
// SLIDE 8 — Business Model & Growth  (dark + duotone grafted)
// =====================================================================
{
  const s = bgSlide(true);
  runningHeader(s, "BUSINESS MODEL & GROWTH", true);
  s.addText("BUSINESS MODEL & GROWTH", { x: 0.9, y: 0.95, w: 8, h: 0.3, fontFace: FONT_BODY, fontSize: 11, bold: true, color: ACCENT, isTextBox: true, charSpacing: 1.5 });
  s.addText("From trusted platform to online store.", {
    x: 0.85, y: 1.28, w: 10, h: 0.75, fontFace: FONT_HEAD, fontSize: 27, bold: true, color: TEXT_LIGHT, isTextBox: true,
  });

  const stages = [
    { icon: "star", tag: "STAGE 1 · BUILD TRUST", title: "Prove it works", body: "Start with phones & tablets. Clear comparisons. Collect real user feedback." },
    { icon: "rocket", tag: "STAGE 2 · EARN REVENUE", title: "Affiliate income first", body: "Earn through affiliate links. Later add price alerts and premium features." },
    { icon: "store", tag: "STAGE 3-4 · GROW", title: "Curated store & expand", body: "Sell selected products directly, then add laptops, watches, and more." },
  ];
  const cw = 3.68, gap = 0.25, startX = 0.7, cy = 2.35, ch = 3.85;
  stages.forEach((st, i) => {
    const cx = startX + i * (cw + gap);
    glassRect(s, { x: cx, y: cy, w: cw, h: ch, r: 0.14, dark: true, fillOpacity: 20, borderOpacity: 35 });
    s.addText(st.tag, { x: cx + 0.3, y: cy + 0.28, w: cw - 0.6, h: 0.3, fontFace: FONT_BODY, fontSize: 9.5, bold: true, color: ACCENT, isTextBox: true, charSpacing: 0.5 });
    iconImg(s, { x: cx + 0.3, y: cy + 0.7, box: 0.5, icon: st.icon, color: ACCENT });
    s.addText(st.title, { x: cx + 0.3, y: cy + 1.45, w: cw - 0.6, h: 0.5, fontFace: FONT_HEAD, fontSize: 15.5, bold: true, color: TEXT_LIGHT, isTextBox: true });
    s.addText(st.body, { x: cx + 0.3, y: cy + 2.0, w: cw - 0.6, h: 1.7, fontFace: FONT_BODY, fontSize: 11, color: TEXT_LIGHT_MUTED, isTextBox: true, lineSpacingMultiple: 1.3 });
  });

  s.addText("We start simple — no warehouse, no inventory — and grow step by step.", {
    x: 0.9, y: 6.55, w: 10.5, h: 0.4, fontFace: FONT_BODY, fontSize: 10.5, italic: true, color: TEXT_LIGHT_MUTED, isTextBox: true,
  });
  // (duotone background effect grafted onto this slide's <p:bg> afterward)
}

// =====================================================================
// SLIDE 9 — Validation Plan
// =====================================================================
{
  const s = bgSlide(false);
  runningHeader(s, "VALIDATION PLAN");
  s.addText("VALIDATION PLAN", { x: 0.9, y: 0.95, w: 6, h: 0.3, fontFace: FONT_BODY, fontSize: 11, bold: true, color: ACCENT, isTextBox: true, charSpacing: 2 });
  s.addText("We won't just assume this works. We'll prove it.", {
    x: 0.85, y: 1.28, w: 11, h: 0.75, fontFace: FONT_HEAD, fontSize: 25, bold: true, color: TEXT_DARK, isTextBox: true,
  });

  glassRect(s, { x: 0.7, y: 2.2, w: 11.9, h: 1.35, r: 0.14, fillOpacity: 80, borderOpacity: 70 });
  iconImg(s, { x: 1.0, y: 2.5, box: 0.5, icon: "quote", color: ACCENT });
  s.addText("If people get a clear score and simple comparisons, they will spend less time researching and feel more confident buying technology.", {
    x: 1.65, y: 2.35, w: 10.6, h: 1.05, valign: "middle", fontFace: FONT_HEAD, fontSize: 14.5, bold: true, italic: true, color: TEXT_DARK, isTextBox: true, lineSpacingMultiple: 1.25,
  });

  const colW = 5.75, gap = 0.4, startX = 0.7, cy = 3.85, ch = 2.85;
  const testItems = [
    { icon: "users", text: "Talk to real potential users" },
    { icon: "code", text: "Build a small working version" },
    { icon: "chart", text: "Track how many click a buy link" },
    { icon: "flask", text: "Collect feedback on confidence" },
  ];
  const successItems = [
    { icon: "compare", text: "Users complete product comparisons" },
    { icon: "database", text: "Users return to check prices" },
    { icon: "handshake", text: "Users click on product links" },
    { icon: "check", text: "Users say the decision felt easier" },
  ];
  glassRect(s, { x: startX, y: cy, w: colW, h: ch, r: 0.14 });
  s.addText("HOW WE'LL TEST IT", { x: startX + 0.3, y: cy + 0.22, w: colW - 0.6, h: 0.3, fontFace: FONT_BODY, fontSize: 10.5, bold: true, color: ACCENT, isTextBox: true, charSpacing: 1.2 });
  let ty1 = cy + 0.68;
  testItems.forEach((it) => {
    iconImg(s, { x: startX + 0.3, y: ty1, box: 0.3, icon: it.icon, color: ACCENT });
    s.addText(it.text, { x: startX + 0.78, y: ty1 - 0.06, w: colW - 1.1, h: 0.45, valign: "middle", fontFace: FONT_BODY, fontSize: 11.5, color: TEXT_DARK, isTextBox: true });
    ty1 += 0.53;
  });

  glassRect(s, { x: startX + colW + gap, y: cy, w: colW, h: ch, r: 0.14 });
  s.addText("WHAT SUCCESS LOOKS LIKE", { x: startX + colW + gap + 0.3, y: cy + 0.22, w: colW - 0.6, h: 0.3, fontFace: FONT_BODY, fontSize: 10.5, bold: true, color: ACCENT, isTextBox: true, charSpacing: 1.2 });
  let ty2 = cy + 0.68;
  successItems.forEach((it) => {
    iconImg(s, { x: startX + colW + gap + 0.3, y: ty2, box: 0.3, icon: it.icon, color: ACCENT });
    s.addText(it.text, { x: startX + colW + gap + 0.78, y: ty2 - 0.06, w: colW - 1.1, h: 0.45, valign: "middle", fontFace: FONT_BODY, fontSize: 11.5, color: TEXT_DARK, isTextBox: true });
    ty2 += 0.53;
  });
}

// =====================================================================
// SLIDE 10 — Closing & Vision  (dark + duotone grafted, trimmed)
// =====================================================================
{
  const s = bgSlide(true);
  wordmark(s, { x: 0.9, y: 0.6, size: 15, dark: true });
  s.addText("Let's make technology\nbuying simple.", {
    x: 0.9, y: 2.05, w: 10.8, h: 1.7, fontFace: FONT_HEAD, fontSize: 40, bold: true, color: TEXT_LIGHT, isTextBox: true, lineSpacingMultiple: 1.08,
  });
  s.addShape(pres.ShapeType.rect, { x: 0.95, y: 3.55, w: 1.6, h: 0.05, fill: { color: ACCENT }, line: { type: "none" } });
  s.addText("Next step: test with real users, then grow into a trusted place to buy.", {
    x: 0.9, y: 3.8, w: 9.8, h: 0.6, fontFace: FONT_BODY, fontSize: 15, color: TEXT_LIGHT_MUTED, isTextBox: true,
  });
  s.addText("TechHub is not just another review website. It is a trusted guide.", {
    x: 0.9, y: 4.5, w: 9.8, h: 0.5, fontFace: FONT_BODY, fontSize: 13, italic: true, color: TEXT_LIGHT_MUTED, isTextBox: true,
  });
  // (team credit line grafted onto this slide afterward)
}

pres.writeFile({ fileName: "new_deck.pptx" }).then(() => console.log("written"));
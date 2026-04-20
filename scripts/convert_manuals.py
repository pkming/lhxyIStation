from __future__ import annotations

import re
import subprocess
from pathlib import Path
from bs4 import BeautifulSoup, Comment, NavigableString, Tag


DOCS_DIR = Path(__file__).resolve().parents[1] / "docs"
MANUALS_DIR = DOCS_DIR / "设备手册"


def normalize_text(value: str) -> str:
    text = value.replace("\xa0", " ")
    text = re.sub(r"\s+", " ", text)
    return text.strip()


def heading_level(text: str) -> int | None:
    if re.match(r"^[一二三四五六七八九十]+、", text):
        return 1
    if re.match(r"^\d+\.\d+\.\d+", text):
        return 3
    if re.match(r"^\d+\.\d+", text):
        return 2
    return None


def strip_attributes(tag: Tag) -> None:
    if tag.name == "img":
        src = tag.get("src")
        alt = tag.get("alt") or Path(src or "").stem
        tag.attrs = {"src": src, "alt": alt}
        return
    if tag.name == "a":
        href = tag.get("href")
        if href:
            tag.attrs = {"href": href}
        else:
            tag.attrs = {}
        return
    if tag.name in {"td", "th"}:
        attrs = {}
        for key in ("colspan", "rowspan"):
            if tag.get(key):
                attrs[key] = tag.get(key)
        tag.attrs = attrs
        return
    tag.attrs = {}


def cleanup_html(source_file: Path) -> Path:
    soup = BeautifulSoup(source_file.read_text(encoding="utf-8"), "lxml")

    for comment in soup.find_all(string=lambda value: isinstance(value, Comment)):
        comment.extract()

    for tag_name in ["script", "style", "xml", "meta", "link", "title", "head", "o:p", "colgroup", "col"]:
        for tag in soup.find_all(tag_name):
            tag.decompose()

    for tag in soup.find_all(["span", "font"]):
        tag.unwrap()

    for tag in soup.find_all(["b"]):
        tag.name = "strong"

    for tag in soup.find_all(["i"]):
        tag.name = "em"

    for tag in soup.find_all("a"):
        if not tag.get("href"):
            tag.unwrap()

    body = soup.body or soup
    for tag in list(body.find_all(True)):
        strip_attributes(tag)

    for tag in list(body.find_all(["div", "section"])):
        tag.unwrap()

    for heading in body.find_all(["h1", "h2", "h3", "h4", "h5", "h6"]):
        text = normalize_text(heading.get_text(" ", strip=True))
        heading.clear()
        if text:
            heading.string = text

    for tag in list(body.find_all(["strong", "em"])):
        if not normalize_text(tag.get_text(" ", strip=True)) and not tag.find("img"):
            tag.unwrap()

    for tag in list(body.find_all("p")):
        text = normalize_text(tag.get_text(" ", strip=True))
        if not text and not tag.find("img"):
            tag.decompose()
            continue

        level = heading_level(text)
        if level is not None and len(text) <= 80 and not tag.find("table"):
            heading = soup.new_tag(f"h{level}")
            heading.string = text
            tag.replace_with(heading)

    html = str(body)
    html = re.sub(r"<p>\s*</p>", "", html)
    html = re.sub(r"\n{3,}", "\n\n", html)

    cleaned_file = source_file.with_suffix(".clean.html")
    cleaned_file.write_text(html, encoding="utf-8")
    return cleaned_file


def convert_manual(folder: Path) -> None:
    html_files = sorted(folder.glob("*.html"))
    if not html_files:
        return

    source_html = html_files[0]
    cleaned_html = cleanup_html(source_html)
    output_file = folder / "README.md"
    subprocess.run(
        [
            "pandoc",
            str(cleaned_html.name),
            "-f",
            "html",
            "-t",
            "gfm+pipe_tables",
            "--wrap=none",
            "-o",
            str(output_file.name),
        ],
        check=True,
        cwd=folder,
    )
    markdown = output_file.read_text(encoding="utf-8")
    markdown = re.sub(r"(?m)^\\\s*$\n?", "", markdown)
    markdown = re.sub(r"(?m)^(#+)\s+\*\*(.+?)\*\*\s*$", r"\1 \2", markdown)
    markdown = re.sub(r"<colgroup>.*?</colgroup>\s*", "", markdown, flags=re.DOTALL)
    markdown = re.sub(r"\n{3,}", "\n\n", markdown)
    output_file.write_text(markdown.strip() + "\n", encoding="utf-8")


def write_index() -> None:
    index_file = MANUALS_DIR / "README.md"
    content = """# 设备手册\n\n- [K80](K80/README.md)：K80 原厂说明书 Markdown 版，保留原始 HTML、DOC 和图片资源。\n- [M90](M90/README.md)：M90 原厂说明书 Markdown 版，保留原始 HTML、DOC 和图片资源。\n\n## 目录约定\n\n- 每个设备一个目录。\n- `README.md` 是可直接阅读的 Markdown 版本。\n- 原始 `html`、`doc` 与 `.files` 图片目录保留在同级，便于重新转换或核对。\n"""
    index_file.write_text(content, encoding="utf-8")


def main() -> None:
    for folder in sorted(MANUALS_DIR.iterdir()):
        if folder.is_dir():
            convert_manual(folder)
    write_index()


if __name__ == "__main__":
    main()
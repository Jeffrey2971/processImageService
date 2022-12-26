function copyText() {
    const text = document.getElementById("text").innerText;
    const input = document.getElementById("input");
    input.value = text;
    input.select();
    document.execCommand("copy");
    document.getElementById("copySuccess").innerText = "已将以下内容复制到粘贴板";
}
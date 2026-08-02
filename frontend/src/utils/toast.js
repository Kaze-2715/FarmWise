export function toast(msg, bg = 'bg-green-500') {
    const box = document.createElement('div')
    box.className = `fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 text-white px-6 py-3 rounded-lg shadow-xl ${bg} z-50`
    box.textContent = msg
    document.body.appendChild(box)
    setTimeout(() => (box.style.opacity = '0'), 2500)
    setTimeout(() => box.remove(), 2800)
}
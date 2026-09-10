export const themes = ['gruvbox', 'mint']

export function getStoredTheme() {
  return localStorage.getItem('theme') || 'gruvbox'
}

export function applyTheme(theme) {
  document.documentElement.setAttribute('data-theme', theme)
  localStorage.setItem('theme', theme)
}

export function nextTheme(current) {
  const idx = themes.indexOf(current)
  return themes[(idx + 1) % themes.length]
}

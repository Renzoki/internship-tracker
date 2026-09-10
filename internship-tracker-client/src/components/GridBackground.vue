<script setup>
import { onMounted, onUnmounted, ref } from 'vue'

const canvasRef = ref(null)

let ctx = null
let width = 0
let height = 0
let resizeObserver = null

const CELL_SIZE = 32

function getThemeColor(varName, fallback) {
  const value = getComputedStyle(document.documentElement)
    .getPropertyValue(varName)
    .trim()
  return value || fallback
}

function hexToRgb(hex) {
  const clean = hex.replace('#', '')
  const bigint = parseInt(
    clean.length === 3
      ? clean.split('').map(c => c + c).join('')
      : clean,
    16
  )
  if (isNaN(bigint)) return { r: 254, g: 128, b: 25 }
  return {
    r: (bigint >> 16) & 255,
    g: (bigint >> 8) & 255,
    b: bigint & 255
  }
}

function drawGrid() {
  if (!ctx) return
  ctx.clearRect(0, 0, width, height)

  const accentHex = getThemeColor('--accent', '#fe8019')
  const accent = hexToRgb(accentHex)

  ctx.strokeStyle = `rgba(${accent.r}, ${accent.g}, ${accent.b}, 0.04)`
  ctx.lineWidth = 1

  ctx.beginPath()
  for (let x = 0; x <= width; x += CELL_SIZE) {
    ctx.moveTo(x, 0)
    ctx.lineTo(x, height)
  }
  for (let y = 0; y <= height; y += CELL_SIZE) {
    ctx.moveTo(0, y)
    ctx.lineTo(width, y)
  }
  ctx.stroke()
}

function resize() {
  const canvas = canvasRef.value
  if (!canvas) return

  const dpr = window.devicePixelRatio || 1
  width = canvas.clientWidth
  height = canvas.clientHeight

  canvas.width = width * dpr
  canvas.height = height * dpr
  ctx.setTransform(dpr, 0, 0, dpr, 0, 0)

  drawGrid()
}

onMounted(() => {
  const canvas = canvasRef.value
  if (!canvas) return

  ctx = canvas.getContext('2d')
  resize()

  resizeObserver = new ResizeObserver(resize)
  resizeObserver.observe(canvas)
})

onUnmounted(() => {
  resizeObserver?.disconnect()
})
</script>

<template>
  <canvas ref="canvasRef" class="bg-canvas" aria-hidden="true"></canvas>
</template>

<style scoped>
.bg-canvas {
  position: fixed;
  inset: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}
</style>

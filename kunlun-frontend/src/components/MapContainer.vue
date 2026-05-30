<template>
  <div ref="mapRef" class="map-container" />
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'

const props = defineProps<{
  center?: [number, number]
  zoom?: number
  markers?: Array<{
    id: number
    lng: number
    lat: number
    name: string
    status: string
  }>
}>()

const emit = defineEmits<{ (e: 'markerClick', id: number): void }>()

const mapRef = ref<HTMLDivElement>()
let map: any = null
const markerMap = new Map<number, any>()

const statusColor: Record<string, string> = {
  SAFE: '#27C46B', WARNING: '#FFAA33', DANGER: '#FF4D4F'
}

function initMap() {
  if (!mapRef.value || !(window as any).AMap) return
  map = new (window as any).AMap.Map(mapRef.value, {
    zoom: props.zoom || 10,
    center: props.center || [103.83, 36.06],
    mapStyle: 'amap://styles/normal',
    viewMode: '2D'
  })
  renderMarkers()
}

function renderMarkers() {
  if (!map || !props.markers) return
  markerMap.forEach(m => map.remove(m))
  markerMap.clear()

  props.markers.forEach(marker => {
    const color = statusColor[marker.status] || '#3B82F6'
    const content = `<div style="
      width:14px;height:14px;border-radius:50%;background:${color};
      box-shadow:0 0 10px ${color};border:2px solid #fff;
    "></div>`
    const m = new (window as any).AMap.Marker({
      position: [marker.lng, marker.lat],
      content,
      anchor: 'center',
      offset: [0, 0]
    })
    m.on('click', () => emit('markerClick', marker.id))
    map.add(m)
    markerMap.set(marker.id, m)
  })
}

watch(() => props.markers, renderMarkers, { deep: true })

onMounted(() => {
  if ((window as any).AMap) {
    initMap()
  } else {
    const script = document.createElement('script')
    script.src = 'https://webapi.amap.com/maps?v=2.0&key=837446c34ab6975dec3092f6a05b58ce'
    script.onload = initMap
    document.head.appendChild(script)
  }
})

onBeforeUnmount(() => {
  if (map) map.destroy()
})

defineExpose({ mapInstance: () => map })
</script>

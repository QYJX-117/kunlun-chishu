import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useBiStore = defineStore('bi', () => {
  const stationStatus = ref<any[]>([])
  const selectedStationId = ref<number | null>(null)

  function setStationStatus(data: any[]) { stationStatus.value = data }
  function selectStation(id: number) { selectedStationId.value = id }

  return { stationStatus, selectedStationId, setStationStatus, selectStation }
})

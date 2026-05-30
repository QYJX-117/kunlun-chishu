import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useMrpStore = defineStore('mrp', () => {
  const running = ref(false)
  const result = ref<any>(null)
  const activeStep = ref(0)

  function setRunning(v: boolean) { running.value = v }
  function setResult(r: any) { result.value = r }
  function setActiveStep(s: number) { activeStep.value = s }

  return { running, result, activeStep, setRunning, setResult, setActiveStep }
})

<template>
  <el-drawer
    :model-value="visible"
    :title="title"
    size="480px"
    direction="rtl"
    @close="$emit('close')"
  >
    <el-form :model="form" label-width="100px" label-position="left">
      <slot :form="form" />
    </el-form>
    <template #footer>
      <el-button @click="$emit('close')">取消</el-button>
      <el-button type="primary" @click="$emit('submit', form)">保存</el-button>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
defineProps<{ visible: boolean; title: string; form: any }>()
defineEmits<{ close: []; submit: [form: any] }>()
</script>

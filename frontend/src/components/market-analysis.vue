<template>
  <main class="mx-auto flex min-h-[65vh] w-full max-w-5xl items-center justify-center">
    <section class="w-full rounded-2xl border border-gray-100 bg-white px-6 py-16 text-center shadow-sm">
      <span class="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-amber-50 text-2xl text-amber-600">
        <i class="fa fa-line-chart"></i>
      </span>
      <h2 class="mt-5 text-2xl font-bold text-gray-800">市场分析服务暂未开放</h2>
      <p class="mx-auto mt-3 max-w-xl text-sm leading-6 text-gray-500">
        产量预测、市场行情、销售建议和价格波动预警尚无后端数据接口。当前页面不再展示模拟结果，待分析服务接入后再开放使用。
      </p>
      <div class="mx-auto mt-8 grid max-w-2xl gap-3 sm:grid-cols-2">
        <div v-for="item in pendingFeatures" :key="item" class="rounded-xl border border-gray-100 bg-gray-50 px-4 py-3 text-sm text-gray-500">
          <i class="fa fa-clock-o mr-2 text-gray-400"></i>{{ item }} · 待接入
        </div>
      </div>
    </section>
  </main>
</template>

<script setup>
const pendingFeatures = ['产量预测', '市场趋势', '销售建议', '价格波动预警']
</script>

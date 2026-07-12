<template>
  <!-- 种植规划 -->
  <section class="bg-white rounded-xl card-shadow p-6 ">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-xl font-bold">种植规划</h2>
      <button
        class="inline-flex items-center rounded-lg border border-green-500 bg-green-500 px-4 py-2 text-sm font-medium text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow active:translate-y-0 active:shadow-sm"
        @click="addPlanModalVisible = true">
        <i class="fa fa-plus mr-1"></i> 新增规划
      </button>
    </div>

    <!-- 种植计划表格 -->
    <div class="overflow-x-auto">
      <table class="min-w-full divide-y divide-gray-200">
        <thead>
          <tr>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              规划名称</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              作物类型</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              种植面积(亩)</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              种植时间</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              状态</th>
            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
              操作</th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <tr v-for="plan in filteredPlans" :key="plan.id">
            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm font-medium">{{ plan.planName }}</div>
            </td>

            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm">{{ plan.cropType }}</div>
            </td>

            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm">{{ plan.area }}</div>
            </td>

            <td class="px-6 py-4 whitespace-nowrap">
              <div class="text-sm">
                {{ plan.plantingTime }} 至 {{ plan.expectedHarvestTime }}
              </div>
            </td>

            <td class="px-6 py-4 whitespace-nowrap">
              <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                {{ plan.status }}
              </span>
            </td>

            <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
              <div class="flex items-center gap-2">
                <button type="button" @click="editPlan(plan)"
                  class="rounded-md border border-primary/30 px-2.5 py-1 text-primary transition-all duration-200 hover:-translate-y-0.5 hover:border-primary hover:shadow-sm active:translate-y-0">
                  编辑
                </button>
                <button type="button" @click="viewPlan(plan)"
                  class="rounded-md border border-gray-300 px-2.5 py-1 text-gray-600 transition-all duration-200 hover:-translate-y-0.5 hover:border-gray-500 hover:shadow-sm active:translate-y-0">
                  查看
                </button>
                <button type="button" @click="openDeletePlanModal(plan)"
                  class="rounded-md border border-red-300 px-2.5 py-1 text-red-500 transition-all duration-200 hover:-translate-y-0.5 hover:border-red-500 hover:shadow-sm active:translate-y-0">
                  删除
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>

<!-- 新增种植计划模态框 -->
<div v-if="addPlanModalVisible" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
  <div class="bg-white rounded-xl w-full max-w-2xl mx-4 overflow-hidden">
    <div class="p-6 border-b border-gray-200">
      <div class="flex justify-between items-center">
        <h3 class="text-lg font-bold">新增种植规划</h3>
        <button class="text-gray-400 hover:text-gray-500" @click="closeAddPlanModal">
          <i class="fa fa-times"></i>
        </button>
      </div>
    </div>
    <div class="p-6">
      <form @submit.prevent="submitNewPlan">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">规划名称</label>
            <input type="text" v-model="newPlanForm.planName"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
              placeholder="如：2024年晚稻种植">
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">作物类型</label>
            <input v-model="newPlanForm.cropType" type="text"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
              placeholder="如：水稻">
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">种植面积(亩)</label>
            <input v-model="newPlanForm.area" type="number" step="0.1"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
              placeholder="请输入种植面积">
          </div>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">种植开始时间</label>
            <input v-model="newPlanForm.plantingTime" type="date"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">预计收获时间</label>
            <input v-model="newPlanForm.expectedHarvestTime" type="date"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
          </div>
        </div>
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">种植地块</label>
          <select name="landId" v-model="newPlanForm.landId"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
            <option value="">请选择地块</option>
            <option v-for="land in lands" :key="land.id" :value="land.id">
              {{ land.name }}
            </option>
          </select>
        </div>
        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-1">备注说明</label>
          <textarea rows="3" v-model="newPlanForm.remark"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
            placeholder="请输入备注信息..."></textarea>
        </div>
        <div class="flex space-x-3">
          <button type="button" class="flex-1 btn-outline" @click="closeAddPlanModal">
            取消
          </button>
          <button type="submit" class="flex-1 btn-primary">
            保存种植规划
          </button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- 编辑种植计划模态框 -->
<div v-if="editPlanModalVisible && selectedPlan"
  class="fixed inset-0 bg-white/30 backdrop-blur-sm flex items-center justify-center z-50">
  <div class="bg-white rounded-xl w-full max-w-2xl mx-4 overflow-hidden">
    <div class="p-6 border-b border-gray-200">
      <div class="flex justify-between items-center">
        <h3 class="text-lg font-bold">编辑种植规划</h3>
        <button @click="closeEditPlanModal" class="text-gray-400 hover:text-gray-500">
          <i class="fa fa-times"></i>
        </button>
      </div>
    </div>
    <div class="p-6">
      <form @submit.prevent="submitEditPlan">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">规划名称</label>
            <input v-model="editPlanForm.planName" type="text"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
              placeholder="如：2024年晚稻种植">
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">作物类型</label>
            <input v-model="editPlanForm.cropType" type="text"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
              placeholder="如：水稻">
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">种植面积(亩)</label>
            <input v-model="editPlanForm.area" type="number" step="0.1"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
              placeholder="请输入种植面积">
          </div>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">种植开始时间</label>
            <input v-model="editPlanForm.plantingTime" type="date"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">预计收获时间</label>
            <input v-model="editPlanForm.expectedHarvestTime" type="date"
              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
          </div>
        </div>
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">种植地块</label>
          <select v-model="editPlanForm.landId" name="landId"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50">
            <option value="">请选择地块</option>
            <option v-for="land in lands" :key="land.id" :value="land.id">
              {{ land.name }}
            </option>
          </select>
        </div>
        <div class="mb-6">
          <label class="block text-sm font-medium text-gray-700 mb-1">备注说明</label>
          <textarea v-model="editPlanForm.remark" rows="3"
            class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary/50"
            placeholder="请输入备注信息..."></textarea>
        </div>
        <div class="flex justify-end space-x-3">
          <button type="button"
            class="w-1/3 rounded-lg border border-gray-500 bg-gray-500 px-4 py-2 text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-gray-600 hover:shadow active:translate-y-0 active:shadow-sm"
            @click="closeEditPlanModal">
            取消
          </button>
          <button type="submit"
            class="w-1/3 rounded-lg border border-green-500 bg-green-500 px-4 py-2 text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow active:translate-y-0 active:shadow-sm">
            保存修改
          </button>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- 查看种植计划模态框 -->
<div v-if="viewPlanModalVisible && selectedPlan"
  class="fixed inset-0 bg-white/30 backdrop-blur-sm flex items-center justify-center z-50">
  <div class="bg-white rounded-xl w-full max-w-2xl mx-4 overflow-hidden">
    <div class="p-6 border-b border-gray-200">
      <div class="flex justify-between items-center">
        <h3 class="text-lg font-bold">查看种植规划</h3>
        <button class="text-gray-400 hover:text-gray-500" @click="closeViewPlanModal">
          <i class="fa fa-times"></i>
        </button>
      </div>
    </div>
    <div class="p-6">
      <div class="space-y-4">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">规划名称</label>
            <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{
              selectedPlan.planName }}</div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">作物类型</label>
            <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{
              selectedPlan.cropType }}</div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">种植面积(亩)</label>
            <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{
              selectedPlan.area }} 亩</div>
          </div>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">种植开始时间</label>
            <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{
              selectedPlan.plantingTime }}</div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">预计收获时间</label>
            <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{
              selectedPlan.expectedHarvestTime }}</div>
          </div>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">种植地块</label>
          <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{
            getLandName(selectedPlan.landId) ||
            '未选择' }}</div>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">备注说明</label>
          <div class="w-full px-3 py-2 border border-gray-300 rounded-lg bg-gray-50">{{
            selectedPlan.remark ||
            '无备注' }}</div>
        </div>
      </div>
      <div class="mt-6 flex justify-end">
        <button type="button"
          class="w-1/3 rounded-lg border border-green-500 bg-green-500 px-4 py-2 text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-green-600 hover:shadow active:translate-y-0 active:shadow-sm"
          @click="closeViewPlanModal">
          关闭
        </button>
      </div>
    </div>
  </div>
</div>

<!-- 删除种植计划确认框 -->
<div v-if="deletePlanModalVisible && selectedDeletePlan"
  class="fixed inset-0 bg-white/30 backdrop-blur-sm flex items-center justify-center z-50">
  <div class="bg-white rounded-xl w-full max-w-sm mx-4 overflow-hidden shadow-xl">
    <div class="p-5 border-b border-gray-200">
      <div class="flex justify-between items-center">
        <h3 class="text-lg font-bold">确认删除</h3>
        <button class="text-gray-400 hover:text-gray-500" @click="closeDeletePlanModal">
          <i class="fa fa-times"></i>
        </button>
      </div>
    </div>
    <div class="p-5">
      <p class="text-center text-sm text-gray-600">
        是否确认删除「{{ selectedDeletePlan.planName }}」？
      </p>
      <div class="mt-6 flex justify-center space-x-3">
        <button type="button"
          class="w-1/3 rounded-lg border border-gray-500 bg-gray-500 px-4 py-2 text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-gray-600 hover:shadow active:translate-y-0 active:shadow-sm"
          @click="closeDeletePlanModal">
          取消
        </button>
        <button type="button"
          class="w-1/3 rounded-lg border border-red-500 bg-red-500 px-4 py-2 text-white shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:bg-red-600 hover:shadow active:translate-y-0 active:shadow-sm"
          @click="confirmDeletePlan">
          删除
        </button>
      </div>
    </div>
  </div>
</div>


</template>

<script setup>
import { computed, ref } from "vue";
import { useFarmStore } from "../../composables/useFarmStore";

const props = defineProps({
  landId: {
    type: String,
    required: true
  }
});

const { plans, lands } = useFarmStore();

const filteredPlans = computed(() =>
  plans.value.filter(plan => plan.landId === props.landId)
);

const addPlanModalVisible = ref(false);
const editPlanModalVisible = ref(false);
const viewPlanModalVisible = ref(false);
const deletePlanModalVisible = ref(false);
const selectedPlan = ref(null);
const selectedDeletePlan = ref(null);

const createEmptyPlanForm = () => ({
  id: null,
  planName: '',
  cropType: '',
  area: '',
  plantingTime: '',
  expectedHarvestTime: '',
  landId: '',
  remark: ''
});

const newPlanForm = ref(createEmptyPlanForm());
const editPlanForm = ref(createEmptyPlanForm());

const closeAddPlanModal = () => {
  newPlanForm.value = createEmptyPlanForm();
  addPlanModalVisible.value = false;
};

const closeEditPlanModal = () => {
  editPlanForm.value = createEmptyPlanForm();
  selectedPlan.value = null;
  editPlanModalVisible.value = false;
};

const closeViewPlanModal = () => {
  selectedPlan.value = null;
  viewPlanModalVisible.value = false;
};

const closeDeletePlanModal = () => {
  selectedDeletePlan.value = null;
  deletePlanModalVisible.value = false;
};

const validatePlanForm = (form) => {
  if (!form.planName.trim()) return alert('请输入规划名称'), false;
  if (!form.cropType.trim()) return alert('请输入作物类型'), false;
  if (!form.area) return alert('请输入种植面积'), false;
  if (!form.plantingTime) return alert('请选择种植开始时间'), false;
  if (!form.expectedHarvestTime) return alert('请选择预计收获时间'), false;
  if (form.expectedHarvestTime < form.plantingTime) return alert('预计收获时间不能早于种植开始时间'), false;
  if (!form.landId) return alert('请选择种植地块'), false;
  return true;
};

const submitNewPlan = () => {
  const form = newPlanForm.value;
  if (!validatePlanForm(form)) return;
  plans.value.push({ ...form, id: Date.now(), status: '待开始' });
  closeAddPlanModal();
};

const openDeletePlanModal = (plan) => {
  selectedDeletePlan.value = plan;
  deletePlanModalVisible.value = true;
};

const confirmDeletePlan = () => {
  if (!selectedDeletePlan.value) return;
  plans.value = plans.value.filter(plan => plan.id !== selectedDeletePlan.value.id);
  closeDeletePlanModal();
};

const viewPlan = (plan) => {
  selectedPlan.value = plan;
  viewPlanModalVisible.value = true;
};

const editPlan = (plan) => {
  selectedPlan.value = plan;
  editPlanForm.value = { ...plan };
  editPlanModalVisible.value = true;
};

const submitEditPlan = () => {
  const form = editPlanForm.value;
  if (!validatePlanForm(form)) return;
  const index = plans.value.findIndex(plan => plan.id === form.id);
  if (index === -1) return;
  plans.value[index] = { ...plans.value[index], ...form };
  closeEditPlanModal();
};

const getLandName = (landId) => lands.value.find(land => land.id === landId)?.name || '未知土地';
</script>

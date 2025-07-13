<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-content">
            <div class="stats-icon" style="background: #409EFF;">
              <el-icon><Goods /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ stats.totalProducts }}</div>
              <div class="stats-label">商品总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-content">
            <div class="stats-icon" style="background: #67C23A;">
              <el-icon><List /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ stats.totalOrders }}</div>
              <div class="stats-label">订单总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-content">
            <div class="stats-icon" style="background: #E6A23C;">
              <el-icon><User /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ stats.totalUsers }}</div>
              <div class="stats-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card" shadow="hover">
          <div class="stats-content">
            <div class="stats-icon" style="background: #F56C6C;">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">¥{{ stats.totalRevenue }}</div>
              <div class="stats-label">总收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>商品分类分布</span>
            </div>
          </template>
          <div class="chart-container">
            <div class="chart-placeholder">
              <el-icon><PieChart /></el-icon>
              <p>商品分类分布图表</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>销售趋势</span>
            </div>
          </template>
          <div class="chart-container">
            <div class="chart-placeholder">
              <el-icon><TrendCharts /></el-icon>
              <p>销售趋势图表</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最新动态 -->
    <el-row :gutter="20" class="activity-row">
      <el-col :span="12">
        <el-card class="activity-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>最新订单</span>
              <el-button type="text">查看全部</el-button>
            </div>
          </template>
          <div class="activity-list">
            <div v-for="order in recentOrders" :key="order.id" class="activity-item">
              <div class="activity-icon">
                <el-icon><List /></el-icon>
              </div>
              <div class="activity-content">
                <div class="activity-title">订单 #{{ order.orderNumber }}</div>
                <div class="activity-desc">{{ order.customerName }} 购买了 {{ order.productName }}</div>
                <div class="activity-time">{{ order.createTime }}</div>
              </div>
              <div class="activity-amount">
                ¥{{ order.amount }}
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="activity-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>系统公告</span>
              <el-button type="text">查看全部</el-button>
            </div>
          </template>
          <div class="activity-list">
            <div v-for="notice in systemNotices" :key="notice.id" class="activity-item">
              <div class="activity-icon">
                <el-icon><Bell /></el-icon>
              </div>
              <div class="activity-content">
                <div class="activity-title">{{ notice.title }}</div>
                <div class="activity-desc">{{ notice.content }}</div>
                <div class="activity-time">{{ notice.createTime }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { Goods, List, User, Money, PieChart, TrendCharts, Bell } from '@element-plus/icons-vue'

export default {
  name: 'Dashboard',
  components: {
    Goods,
    List,
    User,
    Money,
    PieChart,
    TrendCharts,
    Bell
  },
  setup() {
    const stats = ref({
      totalProducts: 156,
      totalOrders: 89,
      totalUsers: 234,
      totalRevenue: '12,580.00'
    })

    const recentOrders = ref([
      {
        id: 1,
        orderNumber: 'ORD001',
        customerName: '张三',
        productName: '有机大米',
        amount: '29.90',
        createTime: '2024-01-15 14:30'
      },
      {
        id: 2,
        orderNumber: 'ORD002',
        customerName: '李四',
        productName: '新鲜番茄',
        amount: '15.80',
        createTime: '2024-01-15 13:45'
      },
      {
        id: 3,
        orderNumber: 'ORD003',
        customerName: '王五',
        productName: '土鸡蛋',
        amount: '28.80',
        createTime: '2024-01-15 12:20'
      }
    ])

    const systemNotices = ref([
      {
        id: 1,
        title: '系统维护通知',
        content: '系统将于今晚22:00-24:00进行维护升级',
        createTime: '2024-01-15 10:00'
      },
      {
        id: 2,
        title: '新功能上线',
        content: '商品管理功能已优化，支持批量操作',
        createTime: '2024-01-14 16:30'
      },
      {
        id: 3,
        title: '安全提醒',
        content: '请及时修改密码，确保账户安全',
        createTime: '2024-01-14 09:15'
      }
    ])

    onMounted(() => {
      // 这里可以调用API获取真实数据
      console.log('Dashboard mounted')
    })

    return {
      stats,
      recentOrders,
      systemNotices
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  height: 120px;
}

.stats-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
}

.stats-icon .el-icon {
  font-size: 24px;
  color: #fff;
}

.stats-info {
  flex: 1;
}

.stats-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.stats-label {
  font-size: 14px;
  color: #909399;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 300px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 240px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-placeholder {
  text-align: center;
  color: #909399;
}

.chart-placeholder .el-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.activity-row {
  margin-bottom: 20px;
}

.activity-card {
  height: 400px;
}

.activity-list {
  height: 320px;
  overflow-y: auto;
}

.activity-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.activity-icon .el-icon {
  font-size: 18px;
  color: #409EFF;
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.activity-desc {
  font-size: 12px;
  color: #606266;
  margin-bottom: 4px;
}

.activity-time {
  font-size: 12px;
  color: #909399;
}

.activity-amount {
  font-size: 16px;
  font-weight: bold;
  color: #67C23A;
}
</style> 
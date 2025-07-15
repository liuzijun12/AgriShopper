<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1>AgriShopper 管理系统</h1>
        <p>欢迎登录后台管理系统</p>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="account">
          <el-input
            v-model="loginForm.account"
            placeholder="请输入手机号或昵称"
            size="large"
            prefix-icon="User"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item>
          <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            style="width: 100%"
            @click.prevent="handleLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>
      

    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import { login } from '@/api/admin'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const loginFormRef = ref()
    const loading = ref(false)
    
    const loginForm = reactive({
      account: '',
      password: '',
      rememberMe: false
    })
    
    const loginRules = {
      account: [
        { required: true, message: '请输入手机号或昵称', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
      ]
    }
    
    const handleLogin = async () => {
      console.log('=== 开始登录流程 ===')
      try {
        // 1. 表单验证
        console.log('1. 开始表单验证')
        await loginFormRef.value.validate()
        console.log('✅ 表单验证通过')
        
        loading.value = true
        
        // 2. 准备登录数据
        const loginData = {
          account: loginForm.account,
          password: loginForm.password
        }
        console.log('2. 登录数据:', loginData)
        
        // 3. 发送登录请求
        console.log('3. 发送登录请求...')
        const response = await login(loginData)
        console.log('4. 登录响应:', response)
        console.log('响应类型:', typeof response)
        console.log('响应数据结构:', Object.keys(response))
        
        // 4. 处理响应
        if (response && response.code === 200) {
          console.log('✅ 登录成功')
          console.log('响应数据:', response.data)
          
          // 保存登录信息
          localStorage.setItem('adminToken', response.data.accessToken)
          localStorage.setItem('adminInfo', JSON.stringify(response.data.adminInfo))
          
          ElMessage.success('登录成功')
          
          // 5. 跳转到商品管理页面
          console.log('5. 准备跳转到商品管理页面')
          await router.push('/')
          console.log('✅ 跳转完成，当前路由:', router.currentRoute.value.path)
        } else {
          console.log('❌ 登录失败')
          console.log('失败响应:', response)
          ElMessage.error(response?.message || '登录失败')
        }
        
      } catch (error) {
        console.error('❌ 登录异常:', error)
        
        if (error.response) {
          console.error('响应错误:', error.response.data)
          ElMessage.error(error.response.data.message || '登录失败')
        } else if (error.request) {
          console.error('请求错误:', error.request)
          ElMessage.error('网络连接失败，请检查后端服务')
        } else {
          console.error('其他错误:', error.message)
          ElMessage.error('登录失败，请重试')
        }
      } finally {
        loading.value = false
        console.log('=== 登录流程结束 ===')
      }
    }
    
    // 测试跳转功能
    const testJump = async () => {
      console.log('测试跳转功能')
      console.log('当前路由:', router.currentRoute.value.path)
      
      // 模拟登录成功，保存token
      localStorage.setItem('adminToken', 'test-token')
      localStorage.setItem('adminInfo', JSON.stringify({
        id: 1,
        nickname: '测试用户',
        phone: '18330177876'
      }))
      
      try {
        console.log('准备跳转到商品管理页面')
        await router.push('/products')
        console.log('跳转完成，当前路由:', router.currentRoute.value.path)
        ElMessage.success('测试跳转成功')
      } catch (error) {
        console.error('跳转失败:', error)
        ElMessage.error('跳转失败: ' + error.message)
      }
    }
    
    return {
      loginFormRef,
      loginForm,
      loginRules,
      loading,
      handleLogin,
      testJump
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-box {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 400px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h1 {
  color: #333;
  font-size: 24px;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.login-header p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.login-form {
  margin-bottom: 20px;
}

.login-footer {
  text-align: center;
  color: #999;
  font-size: 12px;
  line-height: 1.5;
}

.login-footer p {
  margin: 5px 0;
}
</style> 
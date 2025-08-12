<!-- 视频上传组件 -->
<template>
  <el-upload
    v-model:file-list="fileList"
    class="video-upload"
    :before-upload="handleBeforeUpload"
    :http-request="handleUpload"
    :on-success="handleSuccess"
    :on-error="handleError"
    :on-exceed="handleExceed"
    :accept="props.accept"
    :limit="1"
  >
    <template #trigger>
      <el-button type="primary">
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        {{ uploadBtnText }}
      </el-button>
    </template>

    <template #tip>
      <div class="el-upload__tip">
        {{ uploadTip }}
      </div>
    </template>
  </el-upload>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import type { PropType } from 'vue';
import { ElMessage } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue';
import type { UploadRawFile, UploadRequestOptions, UploadUserFile } from 'element-plus';
import FileAPI, { FileInfo } from '@/api/file.api';

const props = defineProps({
  /**
   * 请求携带的额外参数
   */
  data: {
    type: Object,
    default: () => ({}),
  },
  /**
   * 上传文件的参数名
   */
  name: {
    type: String,
    default: 'file',
  },
  /**
   * 单个文件上传大小限制(单位MB)
   */
  maxFileSize: {
    type: Number,
    default: 50,
  },
  /**
   * 上传文件类型
   */
  accept: {
    type: String,
    default: '.mp4,.webm,.ogg',
  },
  /**
   * 上传按钮文本
   */
  uploadBtnText: {
    type: String,
    default: '上传视频',
  },
  /**
   * 上传提示文本
   */
  uploadTip: {
    type: String,
    default: '支持 MP4/WebM/Ogg 格式，单个视频不超过 50MB',
  },
});

const modelValue = defineModel('modelValue', {
  type: String,
  default: '',
});

const fileList = ref<UploadUserFile[]>([]);

// 监听 modelValue 转换用于显示的 fileList
watch(
  modelValue,
  (value) => {
    if (value) {
      const name = value.substring(value.lastIndexOf('/') + 1);
      fileList.value = [
        {
          name,
          url: value,
          status: 'success',
        },
      ];
    } else {
      fileList.value = [];
    }
  },
  {
    immediate: true,
  },
);

/**
 * 上传前校验
 */
function handleBeforeUpload(file: UploadRawFile) {
  // 校验文件类型：虽然 accept 属性限制了用户在文件选择器中可选的文件类型，但仍需在上传时再次校验文件实际类型，确保符合 accept 的规则
  const acceptTypes = props.accept.split(',').map((type) => type.trim());

  // 检查文件格式是否符合 accept
  const isValidType = acceptTypes.some((type) => {
    if (type.startsWith('.')) {
      // 如果是扩展名 (.mp4, .webm)，检查文件名是否以指定扩展名结尾
      return file.name.toLowerCase().endsWith(type);
    } else {
      // 如果是具体的 MIME 类型 (video/mp4, video/webm)，检查是否完全匹配
      return file.type === type;
    }
  });

  if (!isValidType) {
    ElMessage.warning(`上传文件的格式不正确，仅支持：${props.accept}`);
    return false;
  }

  // 限制文件大小
  if (file.size > props.maxFileSize * 1024 * 1024) {
    ElMessage.warning('上传视频不能大于' + props.maxFileSize + 'MB');
    return false;
  }
  return true;
}

/*
 * 上传文件
 */
function handleUpload(options: UploadRequestOptions) {
  return new Promise((resolve, reject) => {
    const file = options.file;

    const formData = new FormData();
    formData.append(props.name, file);

    // 处理附加参数
    Object.keys(props.data).forEach((key) => {
      formData.append(key, props.data[key]);
    });

    FileAPI.upload(formData)
      .then((data) => {
        resolve(data);
      })
      .catch((error) => {
        reject(error);
      });
  });
}

/**
 * 上传文件超出限制
 */
function handleExceed() {
  ElMessage.warning('只能上传一个视频文件');
}

/**
 * 上传成功回调
 */
const handleSuccess = (fileInfo: FileInfo, uploadFile: UploadUserFile) => {
  ElMessage.success('上传成功');
  modelValue.value = fileInfo.url;
};

/**
 * 上传失败回调
 */
const handleError = (error: any) => {
  console.error('上传失败:', error);
  ElMessage.error('上传失败: ' + error.message);
};
</script>

<style lang="scss" scoped>
.video-upload {
  :deep(.el-upload-list__item) {
    transition: none !important;
  }

  :deep(.el-upload-list__item-status-label) {
    display: none;
  }

  :deep(.el-progress) {
    position: absolute;
    bottom: 20px;
    width: 92%;
  }

  :deep(.el-upload-list__item-name) {
    margin-right: 40px;
  }
}
</style>
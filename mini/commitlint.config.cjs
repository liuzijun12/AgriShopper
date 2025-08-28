module.exports = {
  extends: ["@commitlint/config-conventional"],
  rules: {
    "type-enum": [
      2,
      "always",
      [
        "feat", // 新增功能
        "fix", // 修复缺陷
        "docs", // 文档变更
        "style", // 代码格式
        "refactor", // 代码重构
        "perf", // 性能优化
        "test", // 测试
        "build", // 构建
        "ci", // CI配置
        "revert", // 回滚
        "chore", // 其他
      ],
    ],
    "subject-case": [0],
  },
  prompt: {
    messages: {
      type: "选择提交类型:",
      scope: "选择提交范围（可选）:",
      customScope: "输入自定义范围:",
      subject: "输入变更描述:",
      body: "输入详细描述（可选）:",
      breaking: "输入破坏性变更（可选）:",
      footer: "输入关联issue（可选）:",
      confirmCommit: "确认提交?",
    },
    types: [
      { value: "feat", name: "✨ 新增功能", emoji: ":sparkles:" },
      { value: "fix", name: "🐛 修复缺陷", emoji: ":bug:" },
      { value: "docs", name: "📝 文档变更", emoji: ":memo:" },
      { value: "style", name: "💄 代码格式", emoji: ":lipstick:" },
      { value: "refactor", name: "♻️ 代码重构", emoji: ":recycle:" },
      { value: "perf", name: "⚡️ 性能优化", emoji: ":zap:" },
      { value: "test", name: "✅ 测试", emoji: ":white_check_mark:" },
      { value: "build", name: "📦️ 构建", emoji: ":package:" },
      { value: "ci", name: "🎡 CI配置", emoji: ":ferris_wheel:" },
      { value: "revert", name: "⏪️ 回滚", emoji: ":rewind:" },
      { value: "chore", name: "🔨 其他", emoji: ":hammer:" },
    ],
    useEmoji: true,
    emojiAlign: "center",
    useAI: false,
    scopes: [],
    allowCustomScopes: true,
    allowEmptyScopes: true,
    upperCaseSubject: false,
    allowBreakingChanges: ["feat", "fix"],
    skipQuestions: [],
    confirmColorize: true,
  },
};

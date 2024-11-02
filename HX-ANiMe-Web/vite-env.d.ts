// vite-env.d.ts
interface ImportMetaEnv {
    VITE_API_URL: string; // 根据你的需求，可以设置为其他类型
  }
  
  interface ImportMeta {
    readonly env: ImportMetaEnv;
  }
  
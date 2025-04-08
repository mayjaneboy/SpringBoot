**node_modules**：存放下载的依赖包

**public**：存放全局的静态文件，比如说网页的 icon

**src：**

- **assets**：存放代码引用的静态文件，比如：css、js、img
- **components**：存放 vue 的组件（可复用的代码块，就叫组件）
- **router**：定义路由文件
- **views**：存放 vue 网页文件
- **App.vue**：vue 页面全局的入口，所有 vue 文件的父级
- **main.js**：代码的配置文件，引入第三方组件或者我们自己定义的一些组件、css、js 等

**index.html**：vue编译成网页才能在浏览器渲染

**jsconfig.json**：内部配置文件

**package.json**：定义依赖的库

**package-lock.json**：用于锁定下载的依赖的版本的一个文件

**vite.config.js**：全局的配置文件
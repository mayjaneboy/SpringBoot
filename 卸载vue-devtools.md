```
npm uninstall vite-plugin-vue-devtools
```

在vite.config.js中删掉该依赖的导入语句：

```
import vueDevTools from 'vite-plugin-vue-devtools'
```

和使用语句

```
vueDevTools(),
```

 
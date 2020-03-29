# Explaining apps-data-copy

To copy additional resources at apps target related to data-config_*.json see following examples:

- Create a folder "apps-data-copy" beside your data-config_*.json
- Add a file e.g. for additional selector rendering:
  - apps-data-copy/MyFile.model.xml
    - For less duplications e.g. use <sly data-sly-include="${'MyFile.html' @  wcmmode='disabled', decoration='false'}"></sly>
- Add a e.g. a custom clientlib folder with custom js or css files:
  - Folder: apps-data-copy/clientlib
  - existing files from generation will be overwritten

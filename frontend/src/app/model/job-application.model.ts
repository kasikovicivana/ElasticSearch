import { FileUploader } from "ng2-file-upload";

export class DocumentDto {
    constructor (
        public isContract: boolean,
        public document: FileUploader,
      ) {  }
}

package com.yosep.order.common

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris

object RestApiDocumentation {

    fun getDocumentRequest(): OperationRequestPreprocessor {  // 1
        return Preprocessors.preprocessRequest(
            modifyUris()
                .scheme("http")
                .host("salt.dev")
                .port(8085)
                .removePort(),
            Preprocessors.prettyPrint()
        )
    }

    fun getDocumentResponse(): OperationResponsePreprocessor {  // 2
        return Preprocessors.preprocessResponse(Preprocessors.prettyPrint())
    }
}
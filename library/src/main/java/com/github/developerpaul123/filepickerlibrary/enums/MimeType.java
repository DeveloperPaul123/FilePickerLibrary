/*
 * Copyright 2014 Paul Tsouchlos
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.developerpaul123.filepickerlibrary.enums;

/**
 * Created by Paul Tsouchlos
 */
public enum MimeType {
    JPEG("image/jpeg"), PNG("image/png"), XML("application/xml"),
    XLS("application/vnd.ms-excel"), XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    DOC("application/msword"), DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    HTML("text/html"), TXT("text/plain"), PDF("application/pdf");

    private final String mMimeType;

    MimeType(String mimeType) {
        mMimeType = mimeType;
    }

    public String getMimeType() {
        return mMimeType;
    }
}

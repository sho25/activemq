begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_comment
comment|/**  * Represents a message which has a typically out of band Binary Large Object  * (BLOB)  *   *   */
end_comment

begin_interface
specifier|public
interface|interface
name|BlobMessage
extends|extends
name|Message
block|{
comment|/**      * Return the input stream to process the BLOB      */
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
throws|,
name|JMSException
function_decl|;
comment|/**      * Returns the URL for the blob if its available as an external URL (such as file, http, ftp etc)      * or null if there is no URL available      */
name|URL
name|getURL
parameter_list|()
throws|throws
name|MalformedURLException
throws|,
name|JMSException
function_decl|;
comment|/**      * The MIME type of the BLOB which can be used to apply different content types to messages.      */
name|String
name|getMimeType
parameter_list|()
function_decl|;
comment|/**      * Sets the MIME type of the BLOB so that a consumer can process things nicely with a Java Activation Framework      * DataHandler      */
name|void
name|setMimeType
parameter_list|(
name|String
name|mimeType
parameter_list|)
function_decl|;
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * The name of the attachment which can be useful information if transmitting files over ActiveMQ      */
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
block|}
end_interface

end_unit


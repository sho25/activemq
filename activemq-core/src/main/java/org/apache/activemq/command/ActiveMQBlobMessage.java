begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|BlobMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|blob
operator|.
name|BlobUploader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|JMSExceptionSupport
import|;
end_import

begin_comment
comment|/**  * An implementation of {@link BlobMessage} for out of band BLOB transfer  *   * @version $Revision: $  * @openwire:marshaller code="29"  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQBlobMessage
extends|extends
name|ActiveMQMessage
implements|implements
name|BlobMessage
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|ACTIVEMQ_BLOB_MESSAGE
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|BINARY_MIME_TYPE
init|=
literal|"application/octet-stream"
decl_stmt|;
specifier|private
name|String
name|remoteBlobUrl
decl_stmt|;
specifier|private
name|String
name|mimeType
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|boolean
name|deletedByBroker
decl_stmt|;
specifier|private
specifier|transient
name|BlobUploader
name|blobUploader
decl_stmt|;
specifier|private
specifier|transient
name|URL
name|url
decl_stmt|;
specifier|public
name|Message
name|copy
parameter_list|()
block|{
name|ActiveMQBlobMessage
name|copy
init|=
operator|new
name|ActiveMQBlobMessage
argument_list|()
decl_stmt|;
name|copy
argument_list|(
name|copy
argument_list|)
expr_stmt|;
return|return
name|copy
return|;
block|}
specifier|private
name|void
name|copy
parameter_list|(
name|ActiveMQBlobMessage
name|copy
parameter_list|)
block|{
name|super
operator|.
name|copy
argument_list|(
name|copy
argument_list|)
expr_stmt|;
name|copy
operator|.
name|setRemoteBlobUrl
argument_list|(
name|getRemoteBlobUrl
argument_list|()
argument_list|)
expr_stmt|;
name|copy
operator|.
name|setMimeType
argument_list|(
name|getMimeType
argument_list|()
argument_list|)
expr_stmt|;
name|copy
operator|.
name|setDeletedByBroker
argument_list|(
name|isDeletedByBroker
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|byte
name|getDataStructureType
parameter_list|()
block|{
return|return
name|DATA_STRUCTURE_TYPE
return|;
block|}
comment|/**      * @openwire:property version=3 cache=false      */
specifier|public
name|String
name|getRemoteBlobUrl
parameter_list|()
block|{
return|return
name|remoteBlobUrl
return|;
block|}
specifier|public
name|void
name|setRemoteBlobUrl
parameter_list|(
name|String
name|remoteBlobUrl
parameter_list|)
block|{
name|this
operator|.
name|remoteBlobUrl
operator|=
name|remoteBlobUrl
expr_stmt|;
name|url
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * The MIME type of the BLOB which can be used to apply different content      * types to messages.      *       * @openwire:property version=3 cache=true      */
specifier|public
name|String
name|getMimeType
parameter_list|()
block|{
if|if
condition|(
name|mimeType
operator|==
literal|null
condition|)
block|{
return|return
name|BINARY_MIME_TYPE
return|;
block|}
return|return
name|mimeType
return|;
block|}
specifier|public
name|void
name|setMimeType
parameter_list|(
name|String
name|mimeType
parameter_list|)
block|{
name|this
operator|.
name|mimeType
operator|=
name|mimeType
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * The name of the attachment which can be useful information if      * transmitting files over ActiveMQ      *       * @openwire:property version=3 cache=false      */
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**      * @openwire:property version=3 cache=false      */
specifier|public
name|boolean
name|isDeletedByBroker
parameter_list|()
block|{
return|return
name|deletedByBroker
return|;
block|}
specifier|public
name|void
name|setDeletedByBroker
parameter_list|(
name|boolean
name|deletedByBroker
parameter_list|)
block|{
name|this
operator|.
name|deletedByBroker
operator|=
name|deletedByBroker
expr_stmt|;
block|}
specifier|public
name|String
name|getJMSXMimeType
parameter_list|()
block|{
return|return
name|getMimeType
argument_list|()
return|;
block|}
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
throws|,
name|JMSException
block|{
name|URL
name|value
init|=
name|getURL
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|value
operator|.
name|openStream
argument_list|()
return|;
block|}
specifier|public
name|URL
name|getURL
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|url
operator|==
literal|null
operator|&&
name|remoteBlobUrl
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|url
operator|=
operator|new
name|URL
argument_list|(
name|remoteBlobUrl
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
name|JMSExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|url
return|;
block|}
specifier|public
name|void
name|setURL
parameter_list|(
name|URL
name|url
parameter_list|)
block|{
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
name|remoteBlobUrl
operator|=
name|url
operator|!=
literal|null
condition|?
name|url
operator|.
name|toExternalForm
argument_list|()
else|:
literal|null
expr_stmt|;
block|}
specifier|public
name|BlobUploader
name|getBlobUploader
parameter_list|()
block|{
return|return
name|blobUploader
return|;
block|}
specifier|public
name|void
name|setBlobUploader
parameter_list|(
name|BlobUploader
name|blobUploader
parameter_list|)
block|{
name|this
operator|.
name|blobUploader
operator|=
name|blobUploader
expr_stmt|;
block|}
specifier|public
name|void
name|onSend
parameter_list|()
throws|throws
name|JMSException
block|{
name|super
operator|.
name|onSend
argument_list|()
expr_stmt|;
comment|// lets ensure we upload the BLOB first out of band before we send the
comment|// message
if|if
condition|(
name|blobUploader
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|URL
name|value
init|=
name|blobUploader
operator|.
name|upload
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|setURL
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|JMSExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit


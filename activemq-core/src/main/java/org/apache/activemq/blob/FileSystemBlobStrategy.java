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
operator|.
name|blob
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

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
name|URISyntaxException
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
name|command
operator|.
name|ActiveMQBlobMessage
import|;
end_import

begin_comment
comment|/**  * {@link BlobUploadStrategy} and {@link BlobDownloadStrategy} implementation which use the local filesystem for storing  * the payload  *  */
end_comment

begin_class
specifier|public
class|class
name|FileSystemBlobStrategy
implements|implements
name|BlobUploadStrategy
implements|,
name|BlobDownloadStrategy
block|{
specifier|private
specifier|final
name|BlobTransferPolicy
name|policy
decl_stmt|;
specifier|private
name|File
name|rootFile
decl_stmt|;
specifier|public
name|FileSystemBlobStrategy
parameter_list|(
specifier|final
name|BlobTransferPolicy
name|policy
parameter_list|)
throws|throws
name|MalformedURLException
throws|,
name|URISyntaxException
block|{
name|this
operator|.
name|policy
operator|=
name|policy
expr_stmt|;
name|createRootFolder
argument_list|()
expr_stmt|;
block|}
comment|/**      * Create the root folder if not exist       *       * @throws MalformedURLException      * @throws URISyntaxException      */
specifier|protected
name|void
name|createRootFolder
parameter_list|()
throws|throws
name|MalformedURLException
throws|,
name|URISyntaxException
block|{
name|rootFile
operator|=
operator|new
name|File
argument_list|(
operator|new
name|URL
argument_list|(
name|policy
operator|.
name|getUploadUrl
argument_list|()
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|rootFile
operator|.
name|exists
argument_list|()
operator|==
literal|false
condition|)
block|{
name|rootFile
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rootFile
operator|.
name|isDirectory
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Given url is not a directory "
operator|+
name|rootFile
argument_list|)
throw|;
block|}
block|}
comment|/*      * (non-Javadoc)      * @see org.apache.activemq.blob.BlobUploadStrategy#uploadFile(org.apache.activemq.command.ActiveMQBlobMessage, java.io.File)      */
specifier|public
name|URL
name|uploadFile
parameter_list|(
name|ActiveMQBlobMessage
name|message
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|JMSException
throws|,
name|IOException
block|{
return|return
name|uploadStream
argument_list|(
name|message
argument_list|,
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|)
return|;
block|}
comment|/*      * (non-Javadoc)      * @see org.apache.activemq.blob.BlobUploadStrategy#uploadStream(org.apache.activemq.command.ActiveMQBlobMessage, java.io.InputStream)      */
specifier|public
name|URL
name|uploadStream
parameter_list|(
name|ActiveMQBlobMessage
name|message
parameter_list|,
name|InputStream
name|in
parameter_list|)
throws|throws
name|JMSException
throws|,
name|IOException
block|{
name|File
name|f
init|=
name|getFile
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|policy
operator|.
name|getBufferSize
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|c
init|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
init|;
name|c
operator|!=
operator|-
literal|1
condition|;
name|c
operator|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
control|)
block|{
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// File.toURL() is deprecated
return|return
name|f
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
return|;
block|}
comment|/*      * (non-Javadoc)      * @see org.apache.activemq.blob.BlobDownloadStrategy#deleteFile(org.apache.activemq.command.ActiveMQBlobMessage)      */
specifier|public
name|void
name|deleteFile
parameter_list|(
name|ActiveMQBlobMessage
name|message
parameter_list|)
throws|throws
name|IOException
throws|,
name|JMSException
block|{
name|File
name|f
init|=
name|getFile
argument_list|(
name|message
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
name|f
operator|.
name|delete
argument_list|()
operator|==
literal|false
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to delete file "
operator|+
name|f
argument_list|)
throw|;
block|}
block|}
comment|/**      * Returns a {@link FileInputStream} for the give {@link ActiveMQBlobMessage}      */
specifier|public
name|InputStream
name|getInputStream
parameter_list|(
name|ActiveMQBlobMessage
name|message
parameter_list|)
throws|throws
name|IOException
throws|,
name|JMSException
block|{
return|return
operator|new
name|FileInputStream
argument_list|(
name|getFile
argument_list|(
name|message
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Return the {@link File} for the {@link ActiveMQBlobMessage}.       *       * @param message      * @return file      * @throws JMSException      * @throws IOException       */
specifier|protected
name|File
name|getFile
parameter_list|(
name|ActiveMQBlobMessage
name|message
parameter_list|)
throws|throws
name|JMSException
throws|,
name|IOException
block|{
if|if
condition|(
name|message
operator|.
name|getURL
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
return|return
operator|new
name|File
argument_list|(
name|message
operator|.
name|getURL
argument_list|()
operator|.
name|toURI
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to open file for message "
operator|+
name|message
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|//replace all : with _ to make windows more happy
name|String
name|fileName
init|=
name|message
operator|.
name|getJMSMessageID
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|":"
argument_list|,
literal|"_"
argument_list|)
decl_stmt|;
return|return
operator|new
name|File
argument_list|(
name|rootFile
argument_list|,
name|fileName
argument_list|)
return|;
block|}
block|}
end_class

end_unit


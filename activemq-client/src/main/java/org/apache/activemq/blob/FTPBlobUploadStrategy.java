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
name|command
operator|.
name|ActiveMQBlobMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|net
operator|.
name|ftp
operator|.
name|FTPClient
import|;
end_import

begin_comment
comment|/**  * A FTP implementation of {@link BlobUploadStrategy}.  */
end_comment

begin_class
specifier|public
class|class
name|FTPBlobUploadStrategy
extends|extends
name|FTPStrategy
implements|implements
name|BlobUploadStrategy
block|{
specifier|public
name|FTPBlobUploadStrategy
parameter_list|(
name|BlobTransferPolicy
name|transferPolicy
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|super
argument_list|(
name|transferPolicy
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
name|FTPClient
name|ftp
init|=
name|createFTP
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|path
init|=
name|url
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|workingDir
init|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|filename
init|=
name|message
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|":"
argument_list|,
literal|"_"
argument_list|)
decl_stmt|;
name|ftp
operator|.
name|setFileType
argument_list|(
name|FTPClient
operator|.
name|BINARY_FILE_TYPE
argument_list|)
expr_stmt|;
name|String
name|url
decl_stmt|;
if|if
condition|(
operator|!
name|ftp
operator|.
name|changeWorkingDirectory
argument_list|(
name|workingDir
argument_list|)
condition|)
block|{
name|url
operator|=
name|this
operator|.
name|url
operator|.
name|toString
argument_list|()
operator|.
name|replaceFirst
argument_list|(
name|this
operator|.
name|url
operator|.
name|getPath
argument_list|()
argument_list|,
literal|""
argument_list|)
operator|+
literal|"/"
expr_stmt|;
block|}
else|else
block|{
name|url
operator|=
name|this
operator|.
name|url
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|ftp
operator|.
name|storeFile
argument_list|(
name|filename
argument_list|,
name|in
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"FTP store failed: "
operator|+
name|ftp
operator|.
name|getReplyString
argument_list|()
argument_list|)
throw|;
block|}
return|return
operator|new
name|URL
argument_list|(
name|url
operator|+
name|filename
argument_list|)
return|;
block|}
finally|finally
block|{
name|ftp
operator|.
name|quit
argument_list|()
expr_stmt|;
name|ftp
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


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
name|FilterInputStream
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
comment|/**  * A FTP implementation for {@link BlobDownloadStrategy}.  */
end_comment

begin_class
specifier|public
class|class
name|FTPBlobDownloadStrategy
extends|extends
name|FTPStrategy
implements|implements
name|BlobDownloadStrategy
block|{
specifier|public
name|FTPBlobDownloadStrategy
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
name|url
operator|=
name|message
operator|.
name|getURL
argument_list|()
expr_stmt|;
specifier|final
name|FTPClient
name|ftp
init|=
name|createFTP
argument_list|()
decl_stmt|;
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
name|file
init|=
name|path
operator|.
name|substring
argument_list|(
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
name|ftp
operator|.
name|changeWorkingDirectory
argument_list|(
name|workingDir
argument_list|)
expr_stmt|;
name|ftp
operator|.
name|setFileType
argument_list|(
name|FTPClient
operator|.
name|BINARY_FILE_TYPE
argument_list|)
expr_stmt|;
name|InputStream
name|input
init|=
operator|new
name|FilterInputStream
argument_list|(
name|ftp
operator|.
name|retrieveFileStream
argument_list|(
name|file
argument_list|)
argument_list|)
block|{
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
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
decl_stmt|;
return|return
name|input
return|;
block|}
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
name|url
operator|=
name|message
operator|.
name|getURL
argument_list|()
expr_stmt|;
specifier|final
name|FTPClient
name|ftp
init|=
name|createFTP
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|url
operator|.
name|getPath
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|ftp
operator|.
name|deleteFile
argument_list|(
name|path
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Delete file failed: "
operator|+
name|ftp
operator|.
name|getReplyString
argument_list|()
argument_list|)
throw|;
block|}
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


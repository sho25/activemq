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
name|HttpURLConnection
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
comment|/**  * A default implementation of {@link BlobDownloadStrategy} which uses the URL  * class to download files or streams from a remote URL  */
end_comment

begin_class
specifier|public
class|class
name|DefaultBlobDownloadStrategy
extends|extends
name|DefaultStrategy
implements|implements
name|BlobDownloadStrategy
block|{
specifier|public
name|DefaultBlobDownloadStrategy
parameter_list|(
name|BlobTransferPolicy
name|transferPolicy
parameter_list|)
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
name|URL
name|value
init|=
name|message
operator|.
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
name|URL
name|url
init|=
name|createMessageURL
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|HttpURLConnection
name|connection
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setRequestMethod
argument_list|(
literal|"DELETE"
argument_list|)
expr_stmt|;
try|try
block|{
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"DELETE failed on: "
operator|+
name|url
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|connection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|isSuccessfulCode
argument_list|(
name|connection
operator|.
name|getResponseCode
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"DELETE was not successful: "
operator|+
name|connection
operator|.
name|getResponseCode
argument_list|()
operator|+
literal|" "
operator|+
name|connection
operator|.
name|getResponseMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit


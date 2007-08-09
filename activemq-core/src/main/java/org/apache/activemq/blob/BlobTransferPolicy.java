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

begin_comment
comment|/**  * The policy for configuring how BLOBs (Binary Large OBjects) are transferred  * out of band between producers, brokers and consumers.  *  * @version $Revision: $  */
end_comment

begin_class
specifier|public
class|class
name|BlobTransferPolicy
block|{
specifier|private
name|String
name|defaultUploadUrl
init|=
literal|"http://localhost:8080/uploads/"
decl_stmt|;
specifier|private
name|String
name|brokerUploadUrl
decl_stmt|;
specifier|private
name|String
name|uploadUrl
decl_stmt|;
specifier|private
name|int
name|bufferSize
init|=
literal|128
operator|*
literal|1024
decl_stmt|;
specifier|private
name|BlobUploadStrategy
name|uploadStrategy
decl_stmt|;
comment|/**      * Returns a copy of this policy object      */
specifier|public
name|BlobTransferPolicy
name|copy
parameter_list|()
block|{
name|BlobTransferPolicy
name|that
init|=
operator|new
name|BlobTransferPolicy
argument_list|()
decl_stmt|;
name|that
operator|.
name|defaultUploadUrl
operator|=
name|this
operator|.
name|defaultUploadUrl
expr_stmt|;
name|that
operator|.
name|brokerUploadUrl
operator|=
name|this
operator|.
name|brokerUploadUrl
expr_stmt|;
name|that
operator|.
name|uploadUrl
operator|=
name|this
operator|.
name|uploadUrl
expr_stmt|;
name|that
operator|.
name|uploadStrategy
operator|=
name|this
operator|.
name|uploadStrategy
expr_stmt|;
return|return
name|that
return|;
block|}
specifier|public
name|String
name|getUploadUrl
parameter_list|()
block|{
if|if
condition|(
name|uploadUrl
operator|==
literal|null
condition|)
block|{
name|uploadUrl
operator|=
name|getBrokerUploadUrl
argument_list|()
expr_stmt|;
if|if
condition|(
name|uploadUrl
operator|==
literal|null
condition|)
block|{
name|uploadUrl
operator|=
name|getDefaultUploadUrl
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|uploadUrl
return|;
block|}
comment|/**      * Sets the upload URL to use explicitly on the client which will      * overload the default or the broker's URL. This allows the client to decide      * where to upload files to irrespective of the brokers configuration.      */
specifier|public
name|void
name|setUploadUrl
parameter_list|(
name|String
name|uploadUrl
parameter_list|)
block|{
name|this
operator|.
name|uploadUrl
operator|=
name|uploadUrl
expr_stmt|;
block|}
specifier|public
name|String
name|getBrokerUploadUrl
parameter_list|()
block|{
return|return
name|brokerUploadUrl
return|;
block|}
comment|/**      * Called by the JMS client when a broker advertises its upload URL      */
specifier|public
name|void
name|setBrokerUploadUrl
parameter_list|(
name|String
name|brokerUploadUrl
parameter_list|)
block|{
name|this
operator|.
name|brokerUploadUrl
operator|=
name|brokerUploadUrl
expr_stmt|;
block|}
specifier|public
name|String
name|getDefaultUploadUrl
parameter_list|()
block|{
return|return
name|defaultUploadUrl
return|;
block|}
comment|/**      * Sets the default upload URL to use if the broker does not      * have a configured upload URL      */
specifier|public
name|void
name|setDefaultUploadUrl
parameter_list|(
name|String
name|defaultUploadUrl
parameter_list|)
block|{
name|this
operator|.
name|defaultUploadUrl
operator|=
name|defaultUploadUrl
expr_stmt|;
block|}
specifier|public
name|BlobUploadStrategy
name|getUploadStrategy
parameter_list|()
block|{
if|if
condition|(
name|uploadStrategy
operator|==
literal|null
condition|)
block|{
name|uploadStrategy
operator|=
name|createUploadStrategy
argument_list|()
expr_stmt|;
block|}
return|return
name|uploadStrategy
return|;
block|}
comment|/**      * Sets the upload strategy to use for uploading BLOBs to some URL      */
specifier|public
name|void
name|setUploadStrategy
parameter_list|(
name|BlobUploadStrategy
name|uploadStrategy
parameter_list|)
block|{
name|this
operator|.
name|uploadStrategy
operator|=
name|uploadStrategy
expr_stmt|;
block|}
specifier|public
name|int
name|getBufferSize
parameter_list|()
block|{
return|return
name|bufferSize
return|;
block|}
comment|/**      * Sets the default buffer size used when uploading or downloading files      */
specifier|public
name|void
name|setBufferSize
parameter_list|(
name|int
name|bufferSize
parameter_list|)
block|{
name|this
operator|.
name|bufferSize
operator|=
name|bufferSize
expr_stmt|;
block|}
specifier|protected
name|BlobUploadStrategy
name|createUploadStrategy
parameter_list|()
block|{
return|return
operator|new
name|DefaultBlobUploadStrategy
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit


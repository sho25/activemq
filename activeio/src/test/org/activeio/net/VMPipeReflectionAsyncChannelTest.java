begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 Hiram Chirino  *  *  Licensed under the Apache License, Version 2.0 (the "License");  *  you may not use this file except in compliance with the License.  *  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|net
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
name|net
operator|.
name|URI
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
name|org
operator|.
name|activeio
operator|.
name|Channel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|ChannelServer
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|VMPipeReflectionAsyncChannelTest
extends|extends
name|SyncChannelTestSupport
block|{
name|VMPipeAsyncChannelFactory
name|factory
init|=
operator|new
name|VMPipeAsyncChannelFactory
argument_list|()
decl_stmt|;
specifier|protected
name|Channel
name|openChannel
parameter_list|(
name|URI
name|connectURI
parameter_list|)
throws|throws
name|IOException
block|{
name|factory
operator|.
name|setForceRefelection
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|factory
operator|.
name|openAsyncChannel
argument_list|(
name|connectURI
argument_list|)
return|;
block|}
specifier|protected
name|ChannelServer
name|bindChannel
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|factory
operator|.
name|setForceRefelection
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|factory
operator|.
name|bindAsyncChannel
argument_list|(
operator|new
name|URI
argument_list|(
literal|"vmpipe://testpipe"
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit


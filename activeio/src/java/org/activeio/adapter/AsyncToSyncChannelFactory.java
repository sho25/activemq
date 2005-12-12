begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2004 Hiram Chirino  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|adapter
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
name|org
operator|.
name|activeio
operator|.
name|AsyncChannelFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|SyncChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|SyncChannelFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|SyncChannelServer
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|final
specifier|public
class|class
name|AsyncToSyncChannelFactory
implements|implements
name|SyncChannelFactory
block|{
specifier|private
name|AsyncChannelFactory
name|asyncChannelFactory
decl_stmt|;
specifier|static
specifier|public
name|SyncChannelFactory
name|adapt
parameter_list|(
name|AsyncChannelFactory
name|channelFactory
parameter_list|)
block|{
comment|// It might not need adapting
if|if
condition|(
name|channelFactory
operator|instanceof
name|SyncChannelServer
condition|)
block|{
return|return
operator|(
name|SyncChannelFactory
operator|)
name|channelFactory
return|;
block|}
comment|// Can we just just undo the adaptor
if|if
condition|(
name|channelFactory
operator|.
name|getClass
argument_list|()
operator|==
name|SyncToAsyncChannelFactory
operator|.
name|class
condition|)
block|{
return|return
operator|(
operator|(
name|SyncToAsyncChannelFactory
operator|)
name|channelFactory
operator|)
operator|.
name|getSyncChannelFactory
argument_list|()
return|;
block|}
return|return
operator|new
name|AsyncToSyncChannelFactory
argument_list|(
operator|(
name|AsyncChannelFactory
operator|)
name|channelFactory
argument_list|)
return|;
block|}
specifier|private
name|AsyncToSyncChannelFactory
parameter_list|(
name|AsyncChannelFactory
name|asyncChannelFactory
parameter_list|)
block|{
name|this
operator|.
name|asyncChannelFactory
operator|=
name|asyncChannelFactory
expr_stmt|;
block|}
specifier|public
name|SyncChannel
name|openSyncChannel
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|AsyncToSyncChannel
operator|.
name|adapt
argument_list|(
name|asyncChannelFactory
operator|.
name|openAsyncChannel
argument_list|(
name|location
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|SyncChannelServer
name|bindSyncChannel
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|AsyncToSyncChannelServer
operator|.
name|adapt
argument_list|(
name|asyncChannelFactory
operator|.
name|bindAsyncChannel
argument_list|(
name|location
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|AsyncChannelFactory
name|getAsyncChannelFactory
parameter_list|()
block|{
return|return
name|asyncChannelFactory
return|;
block|}
block|}
end_class

end_unit


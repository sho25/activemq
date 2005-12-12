begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|transport
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
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|Command
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|Response
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.5 $  */
end_comment

begin_class
specifier|public
class|class
name|TransportFilter
implements|implements
name|Transport
implements|,
name|TransportListener
block|{
specifier|final
specifier|protected
name|Transport
name|next
decl_stmt|;
specifier|protected
name|TransportListener
name|commandListener
decl_stmt|;
specifier|public
name|TransportFilter
parameter_list|(
name|Transport
name|next
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
block|}
comment|/**      */
specifier|public
name|void
name|setTransportListener
parameter_list|(
name|TransportListener
name|channelListener
parameter_list|)
block|{
name|this
operator|.
name|commandListener
operator|=
name|channelListener
expr_stmt|;
if|if
condition|(
name|channelListener
operator|==
literal|null
condition|)
name|next
operator|.
name|setTransportListener
argument_list|(
literal|null
argument_list|)
expr_stmt|;
else|else
name|next
operator|.
name|setTransportListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.activemq.Service#start()      * @throws IOException if the next channel has not been set.      */
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|next
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The next channel has not been set."
argument_list|)
throw|;
if|if
condition|(
name|commandListener
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The command listener has not been set."
argument_list|)
throw|;
name|next
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see org.activemq.Service#stop()      */
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|next
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|onCommand
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
name|commandListener
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the next.      */
specifier|public
name|Transport
name|getNext
parameter_list|()
block|{
return|return
name|next
return|;
block|}
comment|/**      * @return Returns the packetListener.      */
specifier|public
name|TransportListener
name|getCommandListener
parameter_list|()
block|{
return|return
name|commandListener
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|next
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|oneway
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|IOException
block|{
name|next
operator|.
name|oneway
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FutureResponse
name|asyncRequest
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|next
operator|.
name|asyncRequest
argument_list|(
name|command
argument_list|)
return|;
block|}
specifier|public
name|Response
name|request
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|next
operator|.
name|request
argument_list|(
name|command
argument_list|)
return|;
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|commandListener
operator|.
name|onException
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Object
name|narrow
parameter_list|(
name|Class
name|target
parameter_list|)
block|{
if|if
condition|(
name|target
operator|.
name|isAssignableFrom
argument_list|(
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
name|next
operator|.
name|narrow
argument_list|(
name|target
argument_list|)
return|;
block|}
block|}
end_class

end_unit


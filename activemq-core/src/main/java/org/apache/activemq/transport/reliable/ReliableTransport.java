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
name|transport
operator|.
name|reliable
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
name|util
operator|.
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|Command
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
name|ReplayCommand
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
name|Response
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
name|openwire
operator|.
name|CommandIdComparator
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
name|transport
operator|.
name|FutureResponse
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
name|transport
operator|.
name|ResponseCorrelator
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
name|transport
operator|.
name|Transport
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
name|transport
operator|.
name|udp
operator|.
name|UdpTransport
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * This interceptor deals with out of order commands together with being able to  * handle dropped commands and the re-requesting dropped commands.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ReliableTransport
extends|extends
name|ResponseCorrelator
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ReliableTransport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|ReplayStrategy
name|replayStrategy
decl_stmt|;
specifier|private
name|SortedSet
argument_list|<
name|Command
argument_list|>
name|commands
init|=
operator|new
name|TreeSet
argument_list|<
name|Command
argument_list|>
argument_list|(
operator|new
name|CommandIdComparator
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|int
name|expectedCounter
init|=
literal|1
decl_stmt|;
specifier|private
name|int
name|replayBufferCommandCount
init|=
literal|50
decl_stmt|;
specifier|private
name|int
name|requestTimeout
init|=
literal|2000
decl_stmt|;
specifier|private
name|ReplayBuffer
name|replayBuffer
decl_stmt|;
specifier|private
name|Replayer
name|replayer
decl_stmt|;
specifier|private
name|UdpTransport
name|udpTransport
decl_stmt|;
specifier|public
name|ReliableTransport
parameter_list|(
name|Transport
name|next
parameter_list|,
name|ReplayStrategy
name|replayStrategy
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|replayStrategy
operator|=
name|replayStrategy
expr_stmt|;
block|}
specifier|public
name|ReliableTransport
parameter_list|(
name|Transport
name|next
parameter_list|,
name|UdpTransport
name|udpTransport
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|next
argument_list|,
name|udpTransport
operator|.
name|getSequenceGenerator
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|udpTransport
operator|=
name|udpTransport
expr_stmt|;
name|this
operator|.
name|replayer
operator|=
name|udpTransport
operator|.
name|createReplayer
argument_list|()
expr_stmt|;
block|}
comment|/**      * Requests that a range of commands be replayed      */
specifier|public
name|void
name|requestReplay
parameter_list|(
name|int
name|fromCommandId
parameter_list|,
name|int
name|toCommandId
parameter_list|)
block|{
name|ReplayCommand
name|replay
init|=
operator|new
name|ReplayCommand
argument_list|()
decl_stmt|;
name|replay
operator|.
name|setFirstNakNumber
argument_list|(
name|fromCommandId
argument_list|)
expr_stmt|;
name|replay
operator|.
name|setLastNakNumber
argument_list|(
name|toCommandId
argument_list|)
expr_stmt|;
try|try
block|{
name|oneway
argument_list|(
name|replay
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|getTransportListener
argument_list|()
operator|.
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Object
name|request
parameter_list|(
name|Object
name|o
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Command
name|command
init|=
operator|(
name|Command
operator|)
name|o
decl_stmt|;
name|FutureResponse
name|response
init|=
name|asyncRequest
argument_list|(
name|command
argument_list|,
literal|null
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Response
name|result
init|=
name|response
operator|.
name|getResult
argument_list|(
name|requestTimeout
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
name|onMissingResponse
argument_list|(
name|command
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Object
name|request
parameter_list|(
name|Object
name|o
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Command
name|command
init|=
operator|(
name|Command
operator|)
name|o
decl_stmt|;
name|FutureResponse
name|response
init|=
name|asyncRequest
argument_list|(
name|command
argument_list|,
literal|null
argument_list|)
decl_stmt|;
while|while
condition|(
name|timeout
operator|>
literal|0
condition|)
block|{
name|int
name|time
init|=
name|timeout
decl_stmt|;
if|if
condition|(
name|timeout
operator|>
name|requestTimeout
condition|)
block|{
name|time
operator|=
name|requestTimeout
expr_stmt|;
block|}
name|Response
name|result
init|=
name|response
operator|.
name|getResult
argument_list|(
name|time
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
name|onMissingResponse
argument_list|(
name|command
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|timeout
operator|-=
name|time
expr_stmt|;
block|}
return|return
name|response
operator|.
name|getResult
argument_list|(
literal|0
argument_list|)
return|;
block|}
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|Command
name|command
init|=
operator|(
name|Command
operator|)
name|o
decl_stmt|;
comment|// lets pass wireformat through
if|if
condition|(
name|command
operator|.
name|isWireFormatInfo
argument_list|()
condition|)
block|{
name|super
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|command
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|ReplayCommand
operator|.
name|DATA_STRUCTURE_TYPE
condition|)
block|{
name|replayCommands
argument_list|(
operator|(
name|ReplayCommand
operator|)
name|command
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|actualCounter
init|=
name|command
operator|.
name|getCommandId
argument_list|()
decl_stmt|;
name|boolean
name|valid
init|=
name|expectedCounter
operator|==
name|actualCounter
decl_stmt|;
if|if
condition|(
operator|!
name|valid
condition|)
block|{
synchronized|synchronized
init|(
name|commands
init|)
block|{
name|int
name|nextCounter
init|=
name|actualCounter
decl_stmt|;
name|boolean
name|empty
init|=
name|commands
operator|.
name|isEmpty
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|empty
condition|)
block|{
name|Command
name|nextAvailable
init|=
name|commands
operator|.
name|first
argument_list|()
decl_stmt|;
name|nextCounter
operator|=
name|nextAvailable
operator|.
name|getCommandId
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|boolean
name|keep
init|=
name|replayStrategy
operator|.
name|onDroppedPackets
argument_list|(
name|this
argument_list|,
name|expectedCounter
argument_list|,
name|actualCounter
argument_list|,
name|nextCounter
argument_list|)
decl_stmt|;
if|if
condition|(
name|keep
condition|)
block|{
comment|// lets add it to the list for later on
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Received out of order command which is being buffered for later: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
name|commands
operator|.
name|add
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|empty
condition|)
block|{
comment|// lets see if the first item in the set is the next
comment|// expected
name|command
operator|=
name|commands
operator|.
name|first
argument_list|()
expr_stmt|;
name|valid
operator|=
name|expectedCounter
operator|==
name|command
operator|.
name|getCommandId
argument_list|()
expr_stmt|;
if|if
condition|(
name|valid
condition|)
block|{
name|commands
operator|.
name|remove
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
while|while
condition|(
name|valid
condition|)
block|{
comment|// we've got a valid header so increment counter
name|replayStrategy
operator|.
name|onReceivedPacket
argument_list|(
name|this
argument_list|,
name|expectedCounter
argument_list|)
expr_stmt|;
name|expectedCounter
operator|++
expr_stmt|;
name|super
operator|.
name|onCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|commands
init|)
block|{
comment|// we could have more commands left
name|valid
operator|=
operator|!
name|commands
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
if|if
condition|(
name|valid
condition|)
block|{
comment|// lets see if the first item in the set is the next
comment|// expected
name|command
operator|=
name|commands
operator|.
name|first
argument_list|()
expr_stmt|;
name|valid
operator|=
name|expectedCounter
operator|==
name|command
operator|.
name|getCommandId
argument_list|()
expr_stmt|;
if|if
condition|(
name|valid
condition|)
block|{
name|commands
operator|.
name|remove
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|public
name|int
name|getBufferedCommandCount
parameter_list|()
block|{
synchronized|synchronized
init|(
name|commands
init|)
block|{
return|return
name|commands
operator|.
name|size
argument_list|()
return|;
block|}
block|}
specifier|public
name|int
name|getExpectedCounter
parameter_list|()
block|{
return|return
name|expectedCounter
return|;
block|}
comment|/**      * This property should never really be set - but is mutable primarily for      * test cases      */
specifier|public
name|void
name|setExpectedCounter
parameter_list|(
name|int
name|expectedCounter
parameter_list|)
block|{
name|this
operator|.
name|expectedCounter
operator|=
name|expectedCounter
expr_stmt|;
block|}
specifier|public
name|int
name|getRequestTimeout
parameter_list|()
block|{
return|return
name|requestTimeout
return|;
block|}
comment|/**      * Sets the default timeout of requests before starting to request commands      * are replayed      */
specifier|public
name|void
name|setRequestTimeout
parameter_list|(
name|int
name|requestTimeout
parameter_list|)
block|{
name|this
operator|.
name|requestTimeout
operator|=
name|requestTimeout
expr_stmt|;
block|}
specifier|public
name|ReplayStrategy
name|getReplayStrategy
parameter_list|()
block|{
return|return
name|replayStrategy
return|;
block|}
specifier|public
name|ReplayBuffer
name|getReplayBuffer
parameter_list|()
block|{
if|if
condition|(
name|replayBuffer
operator|==
literal|null
condition|)
block|{
name|replayBuffer
operator|=
name|createReplayBuffer
argument_list|()
expr_stmt|;
block|}
return|return
name|replayBuffer
return|;
block|}
specifier|public
name|void
name|setReplayBuffer
parameter_list|(
name|ReplayBuffer
name|replayBuffer
parameter_list|)
block|{
name|this
operator|.
name|replayBuffer
operator|=
name|replayBuffer
expr_stmt|;
block|}
specifier|public
name|int
name|getReplayBufferCommandCount
parameter_list|()
block|{
return|return
name|replayBufferCommandCount
return|;
block|}
comment|/**      * Sets the default number of commands which are buffered      */
specifier|public
name|void
name|setReplayBufferCommandCount
parameter_list|(
name|int
name|replayBufferSize
parameter_list|)
block|{
name|this
operator|.
name|replayBufferCommandCount
operator|=
name|replayBufferSize
expr_stmt|;
block|}
specifier|public
name|void
name|setReplayStrategy
parameter_list|(
name|ReplayStrategy
name|replayStrategy
parameter_list|)
block|{
name|this
operator|.
name|replayStrategy
operator|=
name|replayStrategy
expr_stmt|;
block|}
specifier|public
name|Replayer
name|getReplayer
parameter_list|()
block|{
return|return
name|replayer
return|;
block|}
specifier|public
name|void
name|setReplayer
parameter_list|(
name|Replayer
name|replayer
parameter_list|)
block|{
name|this
operator|.
name|replayer
operator|=
name|replayer
expr_stmt|;
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
name|start
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|udpTransport
operator|!=
literal|null
condition|)
block|{
name|udpTransport
operator|.
name|setReplayBuffer
argument_list|(
name|getReplayBuffer
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|replayStrategy
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Property replayStrategy not specified"
argument_list|)
throw|;
block|}
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * Lets attempt to replay the request as a command may have disappeared      */
specifier|protected
name|void
name|onMissingResponse
parameter_list|(
name|Command
name|command
parameter_list|,
name|FutureResponse
name|response
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Still waiting for response on: "
operator|+
name|this
operator|+
literal|" to command: "
operator|+
name|command
operator|+
literal|" sending replay message"
argument_list|)
expr_stmt|;
name|int
name|commandId
init|=
name|command
operator|.
name|getCommandId
argument_list|()
decl_stmt|;
name|requestReplay
argument_list|(
name|commandId
argument_list|,
name|commandId
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|ReplayBuffer
name|createReplayBuffer
parameter_list|()
block|{
return|return
operator|new
name|DefaultReplayBuffer
argument_list|(
name|getReplayBufferCommandCount
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|void
name|replayCommands
parameter_list|(
name|ReplayCommand
name|command
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|replayer
operator|==
literal|null
condition|)
block|{
name|onException
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Cannot replay commands. No replayer property configured"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Processing replay command: "
operator|+
name|command
argument_list|)
expr_stmt|;
block|}
name|getReplayBuffer
argument_list|()
operator|.
name|replayMessages
argument_list|(
name|command
operator|.
name|getFirstNakNumber
argument_list|()
argument_list|,
name|command
operator|.
name|getLastNakNumber
argument_list|()
argument_list|,
name|replayer
argument_list|)
expr_stmt|;
comment|// TODO we could proactively remove ack'd stuff from the replay
comment|// buffer
comment|// if we only have a single client talking to us
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


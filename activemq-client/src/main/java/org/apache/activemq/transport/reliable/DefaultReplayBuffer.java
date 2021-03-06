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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  *   *   */
end_comment

begin_class
specifier|public
class|class
name|DefaultReplayBuffer
implements|implements
name|ReplayBuffer
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DefaultReplayBuffer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|int
name|size
decl_stmt|;
specifier|private
name|ReplayBufferListener
name|listener
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|Object
argument_list|>
name|map
decl_stmt|;
specifier|private
name|int
name|lowestCommandId
init|=
literal|1
decl_stmt|;
specifier|private
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|public
name|DefaultReplayBuffer
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|map
operator|=
name|createMap
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addBuffer
parameter_list|(
name|int
name|commandId
parameter_list|,
name|Object
name|buffer
parameter_list|)
block|{
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
literal|"Adding command ID: "
operator|+
name|commandId
operator|+
literal|" to replay buffer: "
operator|+
name|this
operator|+
literal|" object: "
operator|+
name|buffer
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|int
name|max
init|=
name|size
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|map
operator|.
name|size
argument_list|()
operator|>=
name|max
condition|)
block|{
comment|// lets find things to evict
name|Object
name|evictedBuffer
init|=
name|map
operator|.
name|remove
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
operator|++
name|lowestCommandId
argument_list|)
argument_list|)
decl_stmt|;
name|onEvictedBuffer
argument_list|(
name|lowestCommandId
argument_list|,
name|evictedBuffer
argument_list|)
expr_stmt|;
block|}
name|map
operator|.
name|put
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|commandId
argument_list|)
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setReplayBufferListener
parameter_list|(
name|ReplayBufferListener
name|bufferPoolAdapter
parameter_list|)
block|{
name|this
operator|.
name|listener
operator|=
name|bufferPoolAdapter
expr_stmt|;
block|}
specifier|public
name|void
name|replayMessages
parameter_list|(
name|int
name|fromCommandId
parameter_list|,
name|int
name|toCommandId
parameter_list|,
name|Replayer
name|replayer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|replayer
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No Replayer parameter specified"
argument_list|)
throw|;
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
literal|"Buffer: "
operator|+
name|this
operator|+
literal|" replaying messages from: "
operator|+
name|fromCommandId
operator|+
literal|" to: "
operator|+
name|toCommandId
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|fromCommandId
init|;
name|i
operator|<=
name|toCommandId
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|buffer
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|buffer
operator|=
name|map
operator|.
name|get
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|replayer
operator|.
name|sendBuffer
argument_list|(
name|i
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|Map
argument_list|<
name|Integer
argument_list|,
name|Object
argument_list|>
name|createMap
parameter_list|(
name|int
name|maximumSize
parameter_list|)
block|{
return|return
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Object
argument_list|>
argument_list|(
name|maximumSize
argument_list|)
return|;
block|}
specifier|protected
name|void
name|onEvictedBuffer
parameter_list|(
name|int
name|commandId
parameter_list|,
name|Object
name|buffer
parameter_list|)
block|{
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|onBufferDiscarded
argument_list|(
name|commandId
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|state
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|TransactionId
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_class
specifier|public
class|class
name|TransactionState
block|{
specifier|final
name|TransactionId
name|id
decl_stmt|;
specifier|public
specifier|final
name|ArrayList
name|commands
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|shutdown
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|prepared
decl_stmt|;
specifier|private
name|int
name|preparedResult
decl_stmt|;
specifier|public
name|TransactionState
parameter_list|(
name|TransactionId
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|id
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|addCommand
parameter_list|(
name|Command
name|operation
parameter_list|)
block|{
name|checkShutdown
argument_list|()
expr_stmt|;
name|commands
operator|.
name|add
argument_list|(
name|operation
argument_list|)
expr_stmt|;
block|}
specifier|public
name|List
name|getCommands
parameter_list|()
block|{
return|return
name|commands
return|;
block|}
specifier|private
name|void
name|checkShutdown
parameter_list|()
block|{
if|if
condition|(
name|shutdown
operator|.
name|get
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Disposed"
argument_list|)
throw|;
block|}
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|shutdown
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TransactionId
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
specifier|public
name|void
name|setPrepared
parameter_list|(
name|boolean
name|prepared
parameter_list|)
block|{
name|this
operator|.
name|prepared
operator|=
name|prepared
expr_stmt|;
block|}
specifier|public
name|boolean
name|isPrepared
parameter_list|()
block|{
return|return
name|prepared
return|;
block|}
specifier|public
name|void
name|setPreparedResult
parameter_list|(
name|int
name|preparedResult
parameter_list|)
block|{
name|this
operator|.
name|preparedResult
operator|=
name|preparedResult
expr_stmt|;
block|}
specifier|public
name|int
name|getPreparedResult
parameter_list|()
block|{
return|return
name|preparedResult
return|;
block|}
block|}
end_class

end_unit


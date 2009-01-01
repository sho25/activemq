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
name|store
operator|.
name|amq
package|;
end_package

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
name|Message
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
name|MessageId
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
name|store
operator|.
name|MessageRecoveryListener
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
name|store
operator|.
name|MessageStore
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

begin_class
specifier|final
class|class
name|RecoveryListenerAdapter
implements|implements
name|MessageRecoveryListener
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
name|RecoveryListenerAdapter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|MessageStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|MessageRecoveryListener
name|listener
decl_stmt|;
specifier|private
name|int
name|count
decl_stmt|;
specifier|private
name|MessageId
name|lastRecovered
decl_stmt|;
name|RecoveryListenerAdapter
parameter_list|(
name|MessageStore
name|store
parameter_list|,
name|MessageRecoveryListener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasSpace
parameter_list|()
block|{
return|return
name|listener
operator|.
name|hasSpace
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|recoverMessage
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|listener
operator|.
name|hasSpace
argument_list|()
condition|)
block|{
name|listener
operator|.
name|recoverMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|lastRecovered
operator|=
name|message
operator|.
name|getMessageId
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|recoverMessageReference
parameter_list|(
name|MessageId
name|ref
parameter_list|)
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|this
operator|.
name|store
operator|.
name|getMessage
argument_list|(
name|ref
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
return|return
name|recoverMessage
argument_list|(
name|message
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Message id "
operator|+
name|ref
operator|+
literal|" could not be recovered from the data store - already dispatched"
argument_list|)
throw|;
block|}
block|}
name|MessageId
name|getLastRecoveredMessageId
parameter_list|()
block|{
return|return
name|lastRecovered
return|;
block|}
name|int
name|size
parameter_list|()
block|{
return|return
name|count
return|;
block|}
name|void
name|reset
parameter_list|()
block|{
name|count
operator|=
literal|0
expr_stmt|;
block|}
block|}
end_class

end_unit


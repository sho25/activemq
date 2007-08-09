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
name|broker
operator|.
name|region
operator|.
name|policy
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

begin_comment
comment|/**  * A strategy for choosing which destination is used for dead letter queue  * messages.  *   * @version $Revision: 426366 $  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractDeadLetterStrategy
implements|implements
name|DeadLetterStrategy
block|{
specifier|private
name|boolean
name|processNonPersistent
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|processExpired
init|=
literal|true
decl_stmt|;
specifier|public
name|boolean
name|isSendToDeadLetterQueue
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|boolean
name|result
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
literal|true
expr_stmt|;
if|if
condition|(
operator|!
name|message
operator|.
name|isPersistent
argument_list|()
operator|&&
operator|!
name|processNonPersistent
condition|)
block|{
name|result
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|message
operator|.
name|isExpired
argument_list|()
operator|&&
operator|!
name|processExpired
condition|)
block|{
name|result
operator|=
literal|false
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * @return the processExpired      */
specifier|public
name|boolean
name|isProcessExpired
parameter_list|()
block|{
return|return
name|this
operator|.
name|processExpired
return|;
block|}
comment|/**      * @param processExpired the processExpired to set      */
specifier|public
name|void
name|setProcessExpired
parameter_list|(
name|boolean
name|processExpired
parameter_list|)
block|{
name|this
operator|.
name|processExpired
operator|=
name|processExpired
expr_stmt|;
block|}
comment|/**      * @return the processNonPersistent      */
specifier|public
name|boolean
name|isProcessNonPersistent
parameter_list|()
block|{
return|return
name|this
operator|.
name|processNonPersistent
return|;
block|}
comment|/**      * @param processNonPersistent the processNonPersistent to set      */
specifier|public
name|void
name|setProcessNonPersistent
parameter_list|(
name|boolean
name|processNonPersistent
parameter_list|)
block|{
name|this
operator|.
name|processNonPersistent
operator|=
name|processNonPersistent
expr_stmt|;
block|}
block|}
end_class

end_unit


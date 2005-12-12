begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a>  *  * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|filter
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
name|broker
operator|.
name|region
operator|.
name|MessageReference
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
name|ActiveMQDestination
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
name|Message
import|;
end_import

begin_comment
comment|/**  * MessageEvaluationContext is used to cache selection results.  *   * A message usually has multiple selectors applied against it. Some selector  * have a high cost of evaluating against the message. Those selectors may whish  * to cache evaluation results associated with the message in the  * MessageEvaluationContext.  *   * @version $Revision: 1.4 $  */
end_comment

begin_class
specifier|public
class|class
name|MessageEvaluationContext
block|{
specifier|private
name|MessageReference
name|messageReference
decl_stmt|;
specifier|private
name|boolean
name|loaded
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|dropped
decl_stmt|;
specifier|private
name|Message
name|message
decl_stmt|;
specifier|private
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|public
name|MessageEvaluationContext
parameter_list|()
block|{     }
specifier|public
name|boolean
name|isDropped
parameter_list|()
throws|throws
name|IOException
block|{
name|getMessage
argument_list|()
expr_stmt|;
return|return
name|dropped
return|;
block|}
specifier|public
name|Message
name|getMessage
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|dropped
operator|&&
operator|!
name|loaded
condition|)
block|{
name|loaded
operator|=
literal|true
expr_stmt|;
name|messageReference
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
name|message
operator|=
name|messageReference
operator|.
name|getMessage
argument_list|()
expr_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
name|messageReference
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
name|dropped
operator|=
literal|true
expr_stmt|;
name|loaded
operator|=
literal|false
expr_stmt|;
block|}
block|}
return|return
name|message
return|;
block|}
specifier|public
name|void
name|setMessageReference
parameter_list|(
name|MessageReference
name|messageReference
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|messageReference
operator|!=
name|messageReference
condition|)
block|{
name|clearMessageCache
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|messageReference
operator|=
name|messageReference
expr_stmt|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|clearMessageCache
argument_list|()
expr_stmt|;
name|destination
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
specifier|public
name|void
name|setDestination
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
comment|/**      * A strategy hook to allow per-message caches to be cleared      */
specifier|protected
name|void
name|clearMessageCache
parameter_list|()
block|{
if|if
condition|(
name|loaded
condition|)
block|{
name|messageReference
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
name|message
operator|=
literal|null
expr_stmt|;
name|dropped
operator|=
literal|false
expr_stmt|;
name|loaded
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class

end_unit


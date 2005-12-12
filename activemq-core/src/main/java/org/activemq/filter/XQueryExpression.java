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
name|filter
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_comment
comment|/**  * Used to evaluate an XQuery Expression in a JMS selector.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|XQueryExpression
implements|implements
name|BooleanExpression
block|{
specifier|private
specifier|final
name|String
name|xpath
decl_stmt|;
name|XQueryExpression
parameter_list|(
name|String
name|xpath
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|xpath
operator|=
name|xpath
expr_stmt|;
block|}
specifier|public
name|Object
name|evaluate
parameter_list|(
name|MessageEvaluationContext
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|Boolean
operator|.
name|FALSE
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"XQUERY "
operator|+
name|ConstantExpression
operator|.
name|encodeString
argument_list|(
name|xpath
argument_list|)
return|;
block|}
comment|/**      * @param message      * @return true if the expression evaluates to Boolean.TRUE.      * @throws JMSException      */
specifier|public
name|boolean
name|matches
parameter_list|(
name|MessageEvaluationContext
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|Object
name|object
init|=
name|evaluate
argument_list|(
name|message
argument_list|)
decl_stmt|;
return|return
name|object
operator|!=
literal|null
operator|&&
name|object
operator|==
name|Boolean
operator|.
name|TRUE
return|;
block|}
block|}
end_class

end_unit


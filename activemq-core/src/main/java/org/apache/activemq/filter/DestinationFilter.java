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
name|javax
operator|.
name|jms
operator|.
name|JMSException
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
name|ActiveMQDestination
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
name|util
operator|.
name|JMSExceptionSupport
import|;
end_import

begin_comment
comment|/**  * Represents a filter which only operates on Destinations  *  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|DestinationFilter
implements|implements
name|BooleanExpression
block|{
specifier|public
specifier|static
specifier|final
name|String
name|ANY_DESCENDENT
init|=
literal|">"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ANY_CHILD
init|=
literal|"*"
decl_stmt|;
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
name|matches
argument_list|(
name|message
argument_list|)
condition|?
name|Boolean
operator|.
name|TRUE
else|:
name|Boolean
operator|.
name|FALSE
return|;
block|}
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
try|try
block|{
if|if
condition|(
name|message
operator|.
name|isDropped
argument_list|()
condition|)
return|return
literal|false
return|;
return|return
name|matches
argument_list|(
name|message
operator|.
name|getMessage
argument_list|()
operator|.
name|getDestination
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|JMSExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|abstract
name|boolean
name|matches
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
function_decl|;
specifier|public
specifier|static
name|DestinationFilter
name|parseFilter
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|destination
operator|.
name|isComposite
argument_list|()
condition|)
block|{
return|return
operator|new
name|CompositeDestinationFilter
argument_list|(
name|destination
argument_list|)
return|;
block|}
name|String
index|[]
name|paths
init|=
name|DestinationPath
operator|.
name|getDestinationPaths
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|int
name|idx
init|=
name|paths
operator|.
name|length
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|idx
operator|>=
literal|0
condition|)
block|{
name|String
name|lastPath
init|=
name|paths
index|[
name|idx
index|]
decl_stmt|;
if|if
condition|(
name|lastPath
operator|.
name|equals
argument_list|(
name|ANY_DESCENDENT
argument_list|)
condition|)
block|{
return|return
operator|new
name|PrefixDestinationFilter
argument_list|(
name|paths
argument_list|)
return|;
block|}
else|else
block|{
while|while
condition|(
name|idx
operator|>=
literal|0
condition|)
block|{
name|lastPath
operator|=
name|paths
index|[
name|idx
operator|--
index|]
expr_stmt|;
if|if
condition|(
name|lastPath
operator|.
name|equals
argument_list|(
name|ANY_CHILD
argument_list|)
condition|)
block|{
return|return
operator|new
name|WildcardDestinationFilter
argument_list|(
name|paths
argument_list|)
return|;
block|}
block|}
block|}
block|}
comment|// if none of the paths contain a wildcard then use equality
return|return
operator|new
name|SimpleDestinationFilter
argument_list|(
name|destination
argument_list|)
return|;
block|}
block|}
end_class

end_unit


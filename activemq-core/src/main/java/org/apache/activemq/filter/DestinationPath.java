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
name|command
operator|.
name|Message
import|;
end_import

begin_comment
comment|/**  * Helper class for decomposing a Destination into a number of paths  *  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|DestinationPath
block|{
specifier|protected
specifier|static
specifier|final
name|char
name|SEPARATOR
init|=
literal|'.'
decl_stmt|;
specifier|public
specifier|static
name|String
index|[]
name|getDestinationPaths
parameter_list|(
name|String
name|subject
parameter_list|)
block|{
name|List
name|list
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|int
name|previous
init|=
literal|0
decl_stmt|;
name|int
name|lastIndex
init|=
name|subject
operator|.
name|length
argument_list|()
operator|-
literal|1
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|idx
init|=
name|subject
operator|.
name|indexOf
argument_list|(
name|SEPARATOR
argument_list|,
name|previous
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|subject
operator|.
name|substring
argument_list|(
name|previous
argument_list|,
name|lastIndex
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
name|list
operator|.
name|add
argument_list|(
name|subject
operator|.
name|substring
argument_list|(
name|previous
argument_list|,
name|idx
argument_list|)
argument_list|)
expr_stmt|;
name|previous
operator|=
name|idx
operator|+
literal|1
expr_stmt|;
block|}
name|String
index|[]
name|answer
init|=
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|list
operator|.
name|toArray
argument_list|(
name|answer
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|public
specifier|static
name|String
index|[]
name|getDestinationPaths
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|getDestinationPaths
argument_list|(
name|message
operator|.
name|getDestination
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
index|[]
name|getDestinationPaths
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
return|return
name|getDestinationPaths
argument_list|(
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Converts the paths to a single String seperated by dots.      *      * @param paths      * @return      */
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|String
index|[]
name|paths
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|paths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|SEPARATOR
argument_list|)
expr_stmt|;
block|}
name|String
name|path
init|=
name|paths
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit


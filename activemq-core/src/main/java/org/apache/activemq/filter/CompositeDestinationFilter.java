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

begin_comment
comment|/**  * A {@link DestinationFilter} used for composite destinations  *  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|CompositeDestinationFilter
extends|extends
name|DestinationFilter
block|{
specifier|private
name|DestinationFilter
name|filters
index|[]
decl_stmt|;
specifier|public
name|CompositeDestinationFilter
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|ActiveMQDestination
index|[]
name|destinations
init|=
name|destination
operator|.
name|getCompositeDestinations
argument_list|()
decl_stmt|;
name|filters
operator|=
operator|new
name|DestinationFilter
index|[
name|destinations
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|destinations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ActiveMQDestination
name|childDestination
init|=
name|destinations
index|[
name|i
index|]
decl_stmt|;
name|filters
index|[
name|i
index|]
operator|=
name|DestinationFilter
operator|.
name|parseFilter
argument_list|(
name|childDestination
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|filters
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|filters
index|[
name|i
index|]
operator|.
name|matches
argument_list|(
name|destination
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isWildcard
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit


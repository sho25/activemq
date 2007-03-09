begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|virtual
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
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|broker
operator|.
name|ProducerBrokerExchange
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
name|broker
operator|.
name|region
operator|.
name|Destination
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
name|broker
operator|.
name|region
operator|.
name|DestinationFilter
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
name|broker
operator|.
name|region
operator|.
name|DestinationInterceptor
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|filter
operator|.
name|DestinationMap
import|;
end_import

begin_comment
comment|/**  * Implements<a  * href="http://activemq.apache.org/virtual-destinations.html">Virtual  * Topics</a>.  *   * @org.apache.xbean.XBean  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|VirtualDestinationInterceptor
implements|implements
name|DestinationInterceptor
block|{
specifier|private
name|DestinationMap
name|destinationMap
init|=
operator|new
name|DestinationMap
argument_list|()
decl_stmt|;
specifier|private
name|VirtualDestination
index|[]
name|virtualDestinations
decl_stmt|;
specifier|public
name|Destination
name|intercept
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{
name|Set
name|virtualDestinations
init|=
name|destinationMap
operator|.
name|get
argument_list|(
name|destination
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
decl_stmt|;
name|List
name|destinations
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|virtualDestinations
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|VirtualDestination
name|virtualDestination
init|=
operator|(
name|VirtualDestination
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Destination
name|newNestination
init|=
name|virtualDestination
operator|.
name|intercept
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|destinations
operator|.
name|add
argument_list|(
name|newNestination
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|destinations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|destinations
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
operator|(
name|Destination
operator|)
name|destinations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
else|else
block|{
comment|// should rarely be used but here just in case
return|return
name|createCompositeDestination
argument_list|(
name|destination
argument_list|,
name|destinations
argument_list|)
return|;
block|}
block|}
return|return
name|destination
return|;
block|}
specifier|public
name|VirtualDestination
index|[]
name|getVirtualDestinations
parameter_list|()
block|{
return|return
name|virtualDestinations
return|;
block|}
specifier|public
name|void
name|setVirtualDestinations
parameter_list|(
name|VirtualDestination
index|[]
name|virtualDestinations
parameter_list|)
block|{
name|destinationMap
operator|=
operator|new
name|DestinationMap
argument_list|()
expr_stmt|;
name|this
operator|.
name|virtualDestinations
operator|=
name|virtualDestinations
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
name|virtualDestinations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|VirtualDestination
name|virtualDestination
init|=
name|virtualDestinations
index|[
name|i
index|]
decl_stmt|;
name|destinationMap
operator|.
name|put
argument_list|(
name|virtualDestination
operator|.
name|getVirtualDestination
argument_list|()
argument_list|,
name|virtualDestination
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|Destination
name|createCompositeDestination
parameter_list|(
name|Destination
name|destination
parameter_list|,
specifier|final
name|List
name|destinations
parameter_list|)
block|{
return|return
operator|new
name|DestinationFilter
argument_list|(
name|destination
argument_list|)
block|{
specifier|public
name|void
name|send
parameter_list|(
name|ProducerBrokerExchange
name|context
parameter_list|,
name|Message
name|messageSend
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|destinations
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Destination
name|destination
init|=
operator|(
name|Destination
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|destination
operator|.
name|send
argument_list|(
name|context
argument_list|,
name|messageSend
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
block|}
end_class

end_unit


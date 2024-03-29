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
name|advisory
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
name|broker
operator|.
name|region
operator|.
name|virtual
operator|.
name|CompositeDestination
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
name|virtual
operator|.
name|VirtualDestination
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
name|virtual
operator|.
name|VirtualTopic
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
name|ActiveMQQueue
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
name|DestinationFilter
import|;
end_import

begin_comment
comment|/**  * This class will use a destination filter to see if the activeMQ destination matches  * the given virtual destination  *  */
end_comment

begin_class
specifier|public
class|class
name|DestinationFilterVirtualDestinationMatcher
implements|implements
name|VirtualDestinationMatcher
block|{
comment|/* (non-Javadoc)      * @see org.apache.activemq.advisory.VirtualDestinationMatcher#matches(org.apache.activemq.broker.region.virtual.VirtualDestination)      */
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|VirtualDestination
name|virtualDestination
parameter_list|,
name|ActiveMQDestination
name|activeMQDest
parameter_list|)
block|{
if|if
condition|(
name|virtualDestination
operator|instanceof
name|CompositeDestination
condition|)
block|{
name|DestinationFilter
name|filter
init|=
name|DestinationFilter
operator|.
name|parseFilter
argument_list|(
name|virtualDestination
operator|.
name|getMappedDestinations
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|matches
argument_list|(
name|activeMQDest
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|virtualDestination
operator|instanceof
name|VirtualTopic
condition|)
block|{
name|DestinationFilter
name|filter
init|=
name|DestinationFilter
operator|.
name|parseFilter
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
operator|(
operator|(
name|VirtualTopic
operator|)
name|virtualDestination
operator|)
operator|.
name|getPrefix
argument_list|()
operator|+
name|DestinationFilter
operator|.
name|ANY_DESCENDENT
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|matches
argument_list|(
name|activeMQDest
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
block|}
end_class

end_unit


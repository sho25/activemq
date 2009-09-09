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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_comment
comment|/**  * Configuration options used to control how messages are re-delivered when they  * are rolled back.  *   * @org.apache.xbean.XBean element="redeliveryPolicy"  * @version $Revision: 1.11 $  */
end_comment

begin_class
specifier|public
class|class
name|RedeliveryPolicy
implements|implements
name|Cloneable
implements|,
name|Serializable
block|{
specifier|public
specifier|static
specifier|final
name|int
name|NO_MAXIMUM_REDELIVERIES
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
specifier|static
name|Random
name|randomNumberGenerator
decl_stmt|;
comment|// +/-15% for a 30% spread -cgs
specifier|private
name|double
name|collisionAvoidanceFactor
init|=
literal|0.15d
decl_stmt|;
specifier|private
name|int
name|maximumRedeliveries
init|=
literal|6
decl_stmt|;
specifier|private
name|long
name|initialRedeliveryDelay
init|=
literal|1000L
decl_stmt|;
specifier|private
name|boolean
name|useCollisionAvoidance
decl_stmt|;
specifier|private
name|boolean
name|useExponentialBackOff
decl_stmt|;
specifier|private
name|double
name|backOffMultiplier
init|=
literal|5.0
decl_stmt|;
specifier|public
name|RedeliveryPolicy
parameter_list|()
block|{     }
specifier|public
name|RedeliveryPolicy
name|copy
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
name|RedeliveryPolicy
operator|)
name|clone
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not clone: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|double
name|getBackOffMultiplier
parameter_list|()
block|{
return|return
name|backOffMultiplier
return|;
block|}
specifier|public
name|void
name|setBackOffMultiplier
parameter_list|(
name|double
name|backOffMultiplier
parameter_list|)
block|{
name|this
operator|.
name|backOffMultiplier
operator|=
name|backOffMultiplier
expr_stmt|;
block|}
specifier|public
name|short
name|getCollisionAvoidancePercent
parameter_list|()
block|{
return|return
operator|(
name|short
operator|)
name|Math
operator|.
name|round
argument_list|(
name|collisionAvoidanceFactor
operator|*
literal|100
argument_list|)
return|;
block|}
specifier|public
name|void
name|setCollisionAvoidancePercent
parameter_list|(
name|short
name|collisionAvoidancePercent
parameter_list|)
block|{
name|this
operator|.
name|collisionAvoidanceFactor
operator|=
name|collisionAvoidancePercent
operator|*
literal|0.01d
expr_stmt|;
block|}
specifier|public
name|long
name|getInitialRedeliveryDelay
parameter_list|()
block|{
return|return
name|initialRedeliveryDelay
return|;
block|}
specifier|public
name|void
name|setInitialRedeliveryDelay
parameter_list|(
name|long
name|initialRedeliveryDelay
parameter_list|)
block|{
name|this
operator|.
name|initialRedeliveryDelay
operator|=
name|initialRedeliveryDelay
expr_stmt|;
block|}
specifier|public
name|int
name|getMaximumRedeliveries
parameter_list|()
block|{
return|return
name|maximumRedeliveries
return|;
block|}
specifier|public
name|void
name|setMaximumRedeliveries
parameter_list|(
name|int
name|maximumRedeliveries
parameter_list|)
block|{
name|this
operator|.
name|maximumRedeliveries
operator|=
name|maximumRedeliveries
expr_stmt|;
block|}
specifier|public
name|long
name|getRedeliveryDelay
parameter_list|(
name|long
name|previousDelay
parameter_list|)
block|{
name|long
name|redeliveryDelay
decl_stmt|;
if|if
condition|(
name|previousDelay
operator|==
literal|0
condition|)
block|{
name|redeliveryDelay
operator|=
name|initialRedeliveryDelay
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|useExponentialBackOff
operator|&&
name|backOffMultiplier
operator|>
literal|1
condition|)
block|{
name|redeliveryDelay
operator|=
call|(
name|long
call|)
argument_list|(
name|previousDelay
operator|*
name|backOffMultiplier
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|redeliveryDelay
operator|=
name|previousDelay
expr_stmt|;
block|}
if|if
condition|(
name|useCollisionAvoidance
condition|)
block|{
comment|/*              * First random determines +/-, second random determines how far to              * go in that direction. -cgs              */
name|Random
name|random
init|=
name|getRandomNumberGenerator
argument_list|()
decl_stmt|;
name|double
name|variance
init|=
operator|(
name|random
operator|.
name|nextBoolean
argument_list|()
condition|?
name|collisionAvoidanceFactor
else|:
operator|-
name|collisionAvoidanceFactor
operator|)
operator|*
name|random
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
name|redeliveryDelay
operator|+=
name|redeliveryDelay
operator|*
name|variance
expr_stmt|;
block|}
return|return
name|redeliveryDelay
return|;
block|}
specifier|public
name|boolean
name|isUseCollisionAvoidance
parameter_list|()
block|{
return|return
name|useCollisionAvoidance
return|;
block|}
specifier|public
name|void
name|setUseCollisionAvoidance
parameter_list|(
name|boolean
name|useCollisionAvoidance
parameter_list|)
block|{
name|this
operator|.
name|useCollisionAvoidance
operator|=
name|useCollisionAvoidance
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseExponentialBackOff
parameter_list|()
block|{
return|return
name|useExponentialBackOff
return|;
block|}
specifier|public
name|void
name|setUseExponentialBackOff
parameter_list|(
name|boolean
name|useExponentialBackOff
parameter_list|)
block|{
name|this
operator|.
name|useExponentialBackOff
operator|=
name|useExponentialBackOff
expr_stmt|;
block|}
specifier|protected
specifier|static
specifier|synchronized
name|Random
name|getRandomNumberGenerator
parameter_list|()
block|{
if|if
condition|(
name|randomNumberGenerator
operator|==
literal|null
condition|)
block|{
name|randomNumberGenerator
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
block|}
return|return
name|randomNumberGenerator
return|;
block|}
block|}
end_class

end_unit


begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * Configuration options used to control how messages are re-delivered when they  * are rolled back.  *   * @version $Revision: 1.11 $  */
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
specifier|protected
name|int
name|maximumRedeliveries
init|=
literal|5
decl_stmt|;
specifier|protected
name|long
name|initialRedeliveryDelay
init|=
literal|1000L
decl_stmt|;
specifier|protected
name|boolean
name|useExponentialBackOff
init|=
literal|false
decl_stmt|;
specifier|protected
name|short
name|backOffMultiplier
init|=
literal|5
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
name|short
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
name|short
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
block|}
end_class

end_unit


begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|sampler
operator|.
name|config
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jmeter
operator|.
name|config
operator|.
name|ConfigTestElement
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|sampler
operator|.
name|Sampler
import|;
end_import

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
comment|/**  * Producer configuration bean.  */
end_comment

begin_class
specifier|public
class|class
name|SamplerConfig
extends|extends
name|ConfigTestElement
implements|implements
name|Serializable
block|{
comment|/**      * Default constructor.      */
specifier|public
name|SamplerConfig
parameter_list|()
block|{     }
comment|/**      * Sets the producer sampler filename property.      *      * @param newFilename      */
specifier|public
name|void
name|setFilename
parameter_list|(
name|String
name|newFilename
parameter_list|)
block|{
name|this
operator|.
name|setProperty
argument_list|(
name|Sampler
operator|.
name|FILENAME
argument_list|,
name|newFilename
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the producer sampler filename property.      *      * @return  producer sampler filename      */
specifier|public
name|String
name|getFilename
parameter_list|()
block|{
return|return
name|getPropertyAsString
argument_list|(
name|Sampler
operator|.
name|FILENAME
argument_list|)
return|;
block|}
comment|/**      * Returns the producer sampler url property.      *      * @return url      */
specifier|public
name|String
name|getLabel
parameter_list|()
block|{
return|return
operator|(
name|this
operator|.
name|getUrl
argument_list|()
operator|)
return|;
block|}
comment|/**          * Sets the producer sampler url property.          *          * @param newUrl          */
specifier|public
name|void
name|setUrl
parameter_list|(
name|String
name|newUrl
parameter_list|)
block|{
name|this
operator|.
name|setProperty
argument_list|(
name|Sampler
operator|.
name|URL
argument_list|,
name|newUrl
argument_list|)
expr_stmt|;
block|}
comment|/**          * Returns the producer sampler url property.          *          * @return producer url          */
specifier|public
name|String
name|getUrl
parameter_list|()
block|{
return|return
name|getPropertyAsString
argument_list|(
name|Sampler
operator|.
name|URL
argument_list|)
return|;
block|}
block|}
end_class

end_unit


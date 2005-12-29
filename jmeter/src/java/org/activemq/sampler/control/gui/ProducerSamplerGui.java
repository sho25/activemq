begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|sampler
operator|.
name|control
operator|.
name|gui
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
name|gui
operator|.
name|LoginConfigGui
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jmeter
operator|.
name|gui
operator|.
name|util
operator|.
name|VerticalPanel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jmeter
operator|.
name|samplers
operator|.
name|gui
operator|.
name|AbstractSamplerGui
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jmeter
operator|.
name|testelement
operator|.
name|TestElement
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jmeter
operator|.
name|util
operator|.
name|JMeterUtils
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
name|config
operator|.
name|gui
operator|.
name|ProducerConfigGui
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
name|Producer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|BorderFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|BorderLayout
import|;
end_import

begin_comment
comment|/**  * Form in JMeter to enter default values for generating the sampler set.  */
end_comment

begin_class
specifier|public
class|class
name|ProducerSamplerGui
extends|extends
name|AbstractSamplerGui
block|{
comment|//private LoginConfigGui loginPanel;
specifier|private
name|ProducerConfigGui
name|TcpDefaultPanel
decl_stmt|;
comment|/**      * Constructor for the ProducerSamplerGui object      */
specifier|public
name|ProducerSamplerGui
parameter_list|()
block|{
name|init
argument_list|()
expr_stmt|;
block|}
comment|/**      * Method for configuring the ProducerSamplerGui      *      * @param element      */
specifier|public
name|void
name|configure
parameter_list|(
name|TestElement
name|element
parameter_list|)
block|{
name|super
operator|.
name|configure
argument_list|(
name|element
argument_list|)
expr_stmt|;
comment|//loginPanel.configure(element);
name|TcpDefaultPanel
operator|.
name|configure
argument_list|(
name|element
argument_list|)
expr_stmt|;
block|}
comment|/**      * Method for creating test elements      *      * @return returns a sampler      */
specifier|public
name|TestElement
name|createTestElement
parameter_list|()
block|{
name|Producer
name|sampler
init|=
operator|new
name|Producer
argument_list|()
decl_stmt|;
name|modifyTestElement
argument_list|(
name|sampler
argument_list|)
expr_stmt|;
return|return
name|sampler
return|;
block|}
comment|/**      * Method to modify test elements      *      * @param sampler      */
specifier|public
name|void
name|modifyTestElement
parameter_list|(
name|TestElement
name|sampler
parameter_list|)
block|{
name|sampler
operator|.
name|clear
argument_list|()
expr_stmt|;
operator|(
operator|(
name|Producer
operator|)
name|sampler
operator|)
operator|.
name|addTestElement
argument_list|(
name|TcpDefaultPanel
operator|.
name|createTestElement
argument_list|()
argument_list|)
expr_stmt|;
comment|//((Producer) sampler).addTestElement(loginPanel.createTestElement());
name|this
operator|.
name|configureTestElement
argument_list|(
name|sampler
argument_list|)
expr_stmt|;
block|}
comment|/**      * Getter method for the LabelResource property.      *      * @return String constant "producer_sample_title"      */
specifier|public
name|String
name|getLabelResource
parameter_list|()
block|{
return|return
literal|"producer_sample_title"
return|;
block|}
comment|/**      * Method to initialize ProducerSamplerGui. Sets up the layout of the GUI.      */
specifier|private
name|void
name|init
parameter_list|()
block|{
name|setLayout
argument_list|(
operator|new
name|BorderLayout
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|setBorder
argument_list|(
name|makeBorder
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|makeTitlePanel
argument_list|()
argument_list|,
name|BorderLayout
operator|.
name|NORTH
argument_list|)
expr_stmt|;
name|VerticalPanel
name|mainPanel
init|=
operator|new
name|VerticalPanel
argument_list|()
decl_stmt|;
name|TcpDefaultPanel
operator|=
operator|new
name|ProducerConfigGui
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|mainPanel
operator|.
name|add
argument_list|(
name|TcpDefaultPanel
argument_list|)
expr_stmt|;
comment|//loginPanel = new LoginConfigGui(false);
comment|//loginPanel.setBorder(BorderFactory.createTitledBorder(JMeterUtils.getResString("login_config")));
comment|//mainPanel.add(loginPanel);
name|add
argument_list|(
name|mainPanel
argument_list|,
name|BorderLayout
operator|.
name|CENTER
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit


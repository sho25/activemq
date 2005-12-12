begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 Protique Ltd  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  **/
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
name|ConfigTestElement
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
name|config
operator|.
name|gui
operator|.
name|AbstractConfigGui
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
name|apache
operator|.
name|jorphan
operator|.
name|util
operator|.
name|JOrphanUtils
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
name|ConsumerSysTest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JTextField
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JComboBox
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JRadioButton
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JPanel
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JLabel
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

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|FlowLayout
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|ActionEvent
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|ActionListener
import|;
end_import

begin_comment
comment|/**  * Consumer configuration gui bean.  */
end_comment

begin_class
specifier|public
class|class
name|ConsumerSysTestConfigGui
extends|extends
name|AbstractConfigGui
block|{
comment|//private final static String FILENAME = "filename";
specifier|private
specifier|final
specifier|static
name|String
name|URL
init|=
literal|"url"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DURABLE
init|=
literal|"durable"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|NONDURABLE
init|=
literal|"nondurable"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TOPIC
init|=
literal|"topic"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|QUEUE
init|=
literal|"queue"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|NOCONSUMER
init|=
literal|"noconsumer"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|NOSUBJECT
init|=
literal|"nosubject"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|CONSUMER_SYS_TEST_CONFIG_TITLE
init|=
literal|"consumer_sys_test_config_title"
decl_stmt|;
specifier|private
name|JTextField
name|setURL
decl_stmt|;
specifier|private
name|JTextField
name|setNoConsumer
decl_stmt|;
specifier|private
name|JTextField
name|setNoSubject
decl_stmt|;
specifier|private
name|JRadioButton
name|setDurable
decl_stmt|;
specifier|private
name|JRadioButton
name|setNonDurable
decl_stmt|;
specifier|private
name|JRadioButton
name|setTopic
decl_stmt|;
specifier|private
name|JRadioButton
name|setQueue
decl_stmt|;
specifier|private
name|boolean
name|displayName
init|=
literal|true
decl_stmt|;
comment|/**      * Default constructor.      */
specifier|public
name|ConsumerSysTestConfigGui
parameter_list|()
block|{
name|this
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructor.      *      * @param displayName - whether to display the name of the consumer.      */
specifier|public
name|ConsumerSysTestConfigGui
parameter_list|(
name|boolean
name|displayName
parameter_list|)
block|{
name|this
operator|.
name|displayName
operator|=
name|displayName
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the consumer configuration title.      *      * @return consumer configuration title      */
specifier|public
name|String
name|getLabelResource
parameter_list|()
block|{
return|return
name|CONSUMER_SYS_TEST_CONFIG_TITLE
return|;
block|}
comment|/**      * Configures the ConsumerConfigGui bean.      *      * @param element - consumer sampler properties.      */
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
name|setURL
operator|.
name|setText
argument_list|(
name|element
operator|.
name|getPropertyAsString
argument_list|(
name|ConsumerSysTest
operator|.
name|URL
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|element
operator|.
name|getProperty
argument_list|(
name|ConsumerSysTest
operator|.
name|DURABLE
argument_list|)
operator|==
literal|null
condition|)
block|{
name|setDurable
operator|.
name|setSelected
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setNonDurable
operator|.
name|setSelected
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|element
operator|.
name|getPropertyAsBoolean
argument_list|(
name|ConsumerSysTest
operator|.
name|DURABLE
argument_list|)
condition|)
block|{
name|setDurable
operator|.
name|setSelected
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setNonDurable
operator|.
name|setSelected
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setDurable
operator|.
name|setSelected
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setNonDurable
operator|.
name|setSelected
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|element
operator|.
name|getProperty
argument_list|(
name|ConsumerSysTest
operator|.
name|TOPIC
argument_list|)
operator|==
literal|null
condition|)
block|{
name|setTopic
operator|.
name|setSelected
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setQueue
operator|.
name|setSelected
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|element
operator|.
name|getPropertyAsBoolean
argument_list|(
name|ConsumerSysTest
operator|.
name|TOPIC
argument_list|)
condition|)
block|{
name|setTopic
operator|.
name|setSelected
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setQueue
operator|.
name|setSelected
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setTopic
operator|.
name|setSelected
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setQueue
operator|.
name|setSelected
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
name|setNoConsumer
operator|.
name|setText
argument_list|(
name|element
operator|.
name|getPropertyAsString
argument_list|(
name|ConsumerSysTest
operator|.
name|NOCONSUMER
argument_list|)
argument_list|)
expr_stmt|;
name|setNoSubject
operator|.
name|setText
argument_list|(
name|element
operator|.
name|getPropertyAsString
argument_list|(
name|ConsumerSysTest
operator|.
name|NOSUBJECT
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a test element.      *      * @return element      */
specifier|public
name|TestElement
name|createTestElement
parameter_list|()
block|{
name|ConfigTestElement
name|element
init|=
operator|new
name|ConfigTestElement
argument_list|()
decl_stmt|;
name|modifyTestElement
argument_list|(
name|element
argument_list|)
expr_stmt|;
return|return
name|element
return|;
block|}
comment|/**      * Sets the consumer sampler properties to the test element.      *      * @param element      */
specifier|public
name|void
name|modifyTestElement
parameter_list|(
name|TestElement
name|element
parameter_list|)
block|{
name|configureTestElement
argument_list|(
name|element
argument_list|)
expr_stmt|;
name|element
operator|.
name|setProperty
argument_list|(
name|ConsumerSysTest
operator|.
name|URL
argument_list|,
name|setURL
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|element
operator|.
name|setProperty
argument_list|(
name|ConsumerSysTest
operator|.
name|DURABLE
argument_list|,
name|JOrphanUtils
operator|.
name|booleanToString
argument_list|(
name|setDurable
operator|.
name|isSelected
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|element
operator|.
name|setProperty
argument_list|(
name|ConsumerSysTest
operator|.
name|TOPIC
argument_list|,
name|JOrphanUtils
operator|.
name|booleanToString
argument_list|(
name|setTopic
operator|.
name|isSelected
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|element
operator|.
name|setProperty
argument_list|(
name|ConsumerSysTest
operator|.
name|NOCONSUMER
argument_list|,
name|setNoConsumer
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|element
operator|.
name|setProperty
argument_list|(
name|ConsumerSysTest
operator|.
name|NOSUBJECT
argument_list|,
name|setNoSubject
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates the URL panel.      *      * @return urlPanel      */
specifier|private
name|JPanel
name|createURLPanel
parameter_list|()
block|{
name|JLabel
name|label
init|=
operator|new
name|JLabel
argument_list|(
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"form_url"
argument_list|)
argument_list|)
decl_stmt|;
name|setURL
operator|=
operator|new
name|JTextField
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|setURL
operator|.
name|setName
argument_list|(
name|URL
argument_list|)
expr_stmt|;
name|label
operator|.
name|setLabelFor
argument_list|(
name|setURL
argument_list|)
expr_stmt|;
name|JPanel
name|urlPanel
init|=
operator|new
name|JPanel
argument_list|(
operator|new
name|BorderLayout
argument_list|(
literal|5
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|urlPanel
operator|.
name|add
argument_list|(
name|label
argument_list|,
name|BorderLayout
operator|.
name|WEST
argument_list|)
expr_stmt|;
name|urlPanel
operator|.
name|add
argument_list|(
name|setURL
argument_list|,
name|BorderLayout
operator|.
name|CENTER
argument_list|)
expr_stmt|;
return|return
name|urlPanel
return|;
block|}
comment|/**      * Creates the durable panel.      *      * @return durablePanel      */
specifier|private
name|JPanel
name|createDurablePanel
parameter_list|()
block|{
name|JLabel
name|labelDeliveryMode
init|=
operator|new
name|JLabel
argument_list|(
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"form_delivery_mode"
argument_list|)
argument_list|)
decl_stmt|;
name|JLabel
name|labelDurable
init|=
operator|new
name|JLabel
argument_list|(
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"form_durable"
argument_list|)
argument_list|)
decl_stmt|;
name|setDurable
operator|=
operator|new
name|JRadioButton
argument_list|()
expr_stmt|;
name|setDurable
operator|.
name|setName
argument_list|(
name|DURABLE
argument_list|)
expr_stmt|;
name|labelDurable
operator|.
name|setLabelFor
argument_list|(
name|setDurable
argument_list|)
expr_stmt|;
name|setDurable
operator|.
name|setActionCommand
argument_list|(
name|DURABLE
argument_list|)
expr_stmt|;
name|setDurable
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|evt
parameter_list|)
block|{
name|jRadioButtonActionPerformedDelivery
argument_list|(
name|evt
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|setDurable
operator|.
name|setSelected
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|JLabel
name|labelNonDurable
init|=
operator|new
name|JLabel
argument_list|(
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"form_non_durable"
argument_list|)
argument_list|)
decl_stmt|;
name|setNonDurable
operator|=
operator|new
name|JRadioButton
argument_list|()
expr_stmt|;
name|setNonDurable
operator|.
name|setName
argument_list|(
name|NONDURABLE
argument_list|)
expr_stmt|;
name|labelNonDurable
operator|.
name|setLabelFor
argument_list|(
name|setNonDurable
argument_list|)
expr_stmt|;
name|setNonDurable
operator|.
name|setActionCommand
argument_list|(
name|NONDURABLE
argument_list|)
expr_stmt|;
name|setNonDurable
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|evt
parameter_list|)
block|{
name|jRadioButtonActionPerformedDelivery
argument_list|(
name|evt
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|setNonDurable
operator|.
name|setSelected
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FlowLayout
name|flow
init|=
operator|new
name|FlowLayout
argument_list|(
name|FlowLayout
operator|.
name|LEFT
argument_list|)
decl_stmt|;
name|flow
operator|.
name|setHgap
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|flow
operator|.
name|setVgap
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|JPanel
name|durablePanel
init|=
operator|new
name|JPanel
argument_list|(
name|flow
argument_list|)
decl_stmt|;
name|durablePanel
operator|.
name|add
argument_list|(
name|labelDeliveryMode
argument_list|)
expr_stmt|;
name|durablePanel
operator|.
name|add
argument_list|(
operator|new
name|JLabel
argument_list|(
literal|"  "
argument_list|)
argument_list|)
expr_stmt|;
name|durablePanel
operator|.
name|add
argument_list|(
name|setDurable
argument_list|)
expr_stmt|;
name|durablePanel
operator|.
name|add
argument_list|(
name|labelDurable
argument_list|)
expr_stmt|;
name|durablePanel
operator|.
name|add
argument_list|(
operator|new
name|JLabel
argument_list|(
literal|"   "
argument_list|)
argument_list|)
expr_stmt|;
name|durablePanel
operator|.
name|add
argument_list|(
name|setNonDurable
argument_list|)
expr_stmt|;
name|durablePanel
operator|.
name|add
argument_list|(
name|labelNonDurable
argument_list|)
expr_stmt|;
return|return
name|durablePanel
return|;
block|}
comment|/**      * Creates the topic panel.      *      * @return topicPanel      */
specifier|private
name|JPanel
name|createTopicPanel
parameter_list|()
block|{
name|JLabel
name|labelMessagingDomain
init|=
operator|new
name|JLabel
argument_list|(
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"messaging_domain"
argument_list|)
argument_list|)
decl_stmt|;
name|JLabel
name|labelTopic
init|=
operator|new
name|JLabel
argument_list|(
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"form_topic"
argument_list|)
argument_list|)
decl_stmt|;
name|setTopic
operator|=
operator|new
name|JRadioButton
argument_list|()
expr_stmt|;
name|setTopic
operator|.
name|setName
argument_list|(
name|TOPIC
argument_list|)
expr_stmt|;
name|labelTopic
operator|.
name|setLabelFor
argument_list|(
name|setTopic
argument_list|)
expr_stmt|;
name|setTopic
operator|.
name|setActionCommand
argument_list|(
name|TOPIC
argument_list|)
expr_stmt|;
name|setTopic
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|evt
parameter_list|)
block|{
name|jRadioButtonActionPerformed
argument_list|(
name|evt
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|setTopic
operator|.
name|setSelected
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|JLabel
name|labelQueue
init|=
operator|new
name|JLabel
argument_list|(
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"form_queue"
argument_list|)
argument_list|)
decl_stmt|;
name|setQueue
operator|=
operator|new
name|JRadioButton
argument_list|()
expr_stmt|;
name|setQueue
operator|.
name|setName
argument_list|(
name|QUEUE
argument_list|)
expr_stmt|;
name|labelQueue
operator|.
name|setLabelFor
argument_list|(
name|setQueue
argument_list|)
expr_stmt|;
name|setQueue
operator|.
name|setActionCommand
argument_list|(
name|QUEUE
argument_list|)
expr_stmt|;
name|setQueue
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|evt
parameter_list|)
block|{
name|jRadioButtonActionPerformed
argument_list|(
name|evt
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|setQueue
operator|.
name|setSelected
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|FlowLayout
name|flow
init|=
operator|new
name|FlowLayout
argument_list|(
name|FlowLayout
operator|.
name|LEFT
argument_list|)
decl_stmt|;
name|flow
operator|.
name|setHgap
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|flow
operator|.
name|setVgap
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|JPanel
name|topicPanel
init|=
operator|new
name|JPanel
argument_list|(
name|flow
argument_list|)
decl_stmt|;
name|topicPanel
operator|.
name|add
argument_list|(
name|labelMessagingDomain
argument_list|)
expr_stmt|;
name|topicPanel
operator|.
name|add
argument_list|(
operator|new
name|JLabel
argument_list|(
literal|"  "
argument_list|)
argument_list|)
expr_stmt|;
name|topicPanel
operator|.
name|add
argument_list|(
name|setTopic
argument_list|)
expr_stmt|;
name|topicPanel
operator|.
name|add
argument_list|(
name|labelTopic
argument_list|)
expr_stmt|;
name|topicPanel
operator|.
name|add
argument_list|(
operator|new
name|JLabel
argument_list|(
literal|"   "
argument_list|)
argument_list|)
expr_stmt|;
name|topicPanel
operator|.
name|add
argument_list|(
name|setQueue
argument_list|)
expr_stmt|;
name|topicPanel
operator|.
name|add
argument_list|(
name|labelQueue
argument_list|)
expr_stmt|;
return|return
name|topicPanel
return|;
block|}
comment|/**      * Creates the no consumer panel.      *      * @return noConsumerPanel      */
specifier|private
name|JPanel
name|createNoConsumerPanel
parameter_list|()
block|{
name|JLabel
name|label
init|=
operator|new
name|JLabel
argument_list|(
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"form_no_consumer"
argument_list|)
argument_list|)
decl_stmt|;
name|setNoConsumer
operator|=
operator|new
name|JTextField
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|setNoConsumer
operator|.
name|setName
argument_list|(
name|NOCONSUMER
argument_list|)
expr_stmt|;
name|label
operator|.
name|setLabelFor
argument_list|(
name|setNoConsumer
argument_list|)
expr_stmt|;
name|JPanel
name|noConsumerPanel
init|=
operator|new
name|JPanel
argument_list|(
operator|new
name|BorderLayout
argument_list|(
literal|5
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|noConsumerPanel
operator|.
name|add
argument_list|(
name|label
argument_list|,
name|BorderLayout
operator|.
name|WEST
argument_list|)
expr_stmt|;
name|noConsumerPanel
operator|.
name|add
argument_list|(
name|setNoConsumer
argument_list|,
name|BorderLayout
operator|.
name|CENTER
argument_list|)
expr_stmt|;
return|return
name|noConsumerPanel
return|;
block|}
comment|/**      * Creates the no subject panel.      *      * @return noSubjectPanel      */
specifier|private
name|JPanel
name|createNoSubjectPanel
parameter_list|()
block|{
name|JLabel
name|label
init|=
operator|new
name|JLabel
argument_list|(
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"form_no_subject"
argument_list|)
argument_list|)
decl_stmt|;
name|setNoSubject
operator|=
operator|new
name|JTextField
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|setNoSubject
operator|.
name|setName
argument_list|(
name|NOSUBJECT
argument_list|)
expr_stmt|;
name|label
operator|.
name|setLabelFor
argument_list|(
name|setNoSubject
argument_list|)
expr_stmt|;
name|JPanel
name|noSubjectPanel
init|=
operator|new
name|JPanel
argument_list|(
operator|new
name|BorderLayout
argument_list|(
literal|5
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|noSubjectPanel
operator|.
name|add
argument_list|(
name|label
argument_list|,
name|BorderLayout
operator|.
name|WEST
argument_list|)
expr_stmt|;
name|noSubjectPanel
operator|.
name|add
argument_list|(
name|setNoSubject
argument_list|,
name|BorderLayout
operator|.
name|CENTER
argument_list|)
expr_stmt|;
return|return
name|noSubjectPanel
return|;
block|}
comment|/**      * Initializes the gui components.      */
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
if|if
condition|(
name|displayName
condition|)
block|{
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
block|}
name|VerticalPanel
name|mainPanel
init|=
operator|new
name|VerticalPanel
argument_list|()
decl_stmt|;
name|mainPanel
operator|.
name|add
argument_list|(
name|createURLPanel
argument_list|()
argument_list|)
expr_stmt|;
name|mainPanel
operator|.
name|add
argument_list|(
name|createNoConsumerPanel
argument_list|()
argument_list|)
expr_stmt|;
name|mainPanel
operator|.
name|add
argument_list|(
name|createNoSubjectPanel
argument_list|()
argument_list|)
expr_stmt|;
name|mainPanel
operator|.
name|add
argument_list|(
name|createDurablePanel
argument_list|()
argument_list|)
expr_stmt|;
name|mainPanel
operator|.
name|add
argument_list|(
name|createTopicPanel
argument_list|()
argument_list|)
expr_stmt|;
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
comment|/**      * Listener action for selecting Messaging Domain.      *      * @param evt - event triggered.      */
specifier|private
name|void
name|jRadioButtonActionPerformed
parameter_list|(
name|ActionEvent
name|evt
parameter_list|)
block|{
name|String
name|evtActionCommand
init|=
name|evt
operator|.
name|getActionCommand
argument_list|()
decl_stmt|;
if|if
condition|(
name|evtActionCommand
operator|.
name|equals
argument_list|(
name|TOPIC
argument_list|)
condition|)
block|{
name|setTopic
operator|.
name|setSelected
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setQueue
operator|.
name|setSelected
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setDurable
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setNonDurable
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setNonDurable
operator|.
name|setSelected
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|evtActionCommand
operator|.
name|equals
argument_list|(
name|QUEUE
argument_list|)
condition|)
block|{
name|setTopic
operator|.
name|setSelected
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setQueue
operator|.
name|setSelected
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setDurable
operator|.
name|setSelected
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setDurable
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setNonDurable
operator|.
name|setSelected
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setNonDurable
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Listener action for selecting Delivery Mode.      *      * @param evt - event triggered.      */
specifier|private
name|void
name|jRadioButtonActionPerformedDelivery
parameter_list|(
name|ActionEvent
name|evt
parameter_list|)
block|{
name|String
name|evtActionCommand
init|=
name|evt
operator|.
name|getActionCommand
argument_list|()
decl_stmt|;
if|if
condition|(
name|evtActionCommand
operator|.
name|equals
argument_list|(
name|DURABLE
argument_list|)
condition|)
block|{
name|setDurable
operator|.
name|setSelected
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setNonDurable
operator|.
name|setSelected
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|evtActionCommand
operator|.
name|equals
argument_list|(
name|NONDURABLE
argument_list|)
condition|)
block|{
name|setDurable
operator|.
name|setSelected
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setNonDurable
operator|.
name|setSelected
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jmeter
operator|.
name|visualizers
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
name|visualizers
operator|.
name|gui
operator|.
name|AbstractVisualizer
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
name|Clearable
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
name|SampleResult
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
name|gui
operator|.
name|ObjectTableModel
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
name|gui
operator|.
name|layout
operator|.
name|VerticalLayout
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
name|reflect
operator|.
name|Functor
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
name|logging
operator|.
name|LoggingManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log
operator|.
name|Logger
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
name|org
operator|.
name|activemq
operator|.
name|sampler
operator|.
name|ProducerSysTest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JTable
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
name|JScrollPane
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
name|BorderFactory
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
name|javax
operator|.
name|swing
operator|.
name|table
operator|.
name|DefaultTableCellRenderer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|border
operator|.
name|Border
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|border
operator|.
name|EmptyBorder
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
name|Color
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
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|EDU
operator|.
name|oswego
operator|.
name|cs
operator|.
name|dl
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  * A tableVisualizer that can display Producer System Test Output.  */
end_comment

begin_class
specifier|public
class|class
name|ConsumerSysTableVisualizer
extends|extends
name|AbstractVisualizer
implements|implements
name|Clearable
block|{
specifier|private
specifier|static
name|Logger
name|log
init|=
name|LoggingManager
operator|.
name|getLoggerForClass
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|boolean
name|msgNotOrdered
init|=
literal|false
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|COLUMNS
init|=
operator|new
name|String
index|[]
block|{
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"table_visualizer_sample_consumerid"
argument_list|)
block|,
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"table_visualizer_sample_consumerseq_number"
argument_list|)
block|,
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"table_visualizer_sample_prodname"
argument_list|)
block|,
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"table_visualizer_sample_producerseq_number"
argument_list|)
block|,
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"table_visualizer_sample_message"
argument_list|)
block|}
decl_stmt|;
specifier|private
name|ObjectTableModel
name|model
init|=
literal|null
decl_stmt|;
specifier|private
name|JTable
name|table
init|=
literal|null
decl_stmt|;
specifier|private
name|JScrollPane
name|tableScrollPanel
init|=
literal|null
decl_stmt|;
specifier|private
name|JTextField
name|messageField
init|=
literal|null
decl_stmt|;
comment|/**      *  Constructor for the TableVisualizer object.      */
specifier|public
name|ConsumerSysTableVisualizer
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|model
operator|=
operator|new
name|ObjectTableModel
argument_list|(
name|COLUMNS
argument_list|,
operator|new
name|Functor
index|[]
block|{
operator|new
name|Functor
argument_list|(
literal|"getConsumerID"
argument_list|)
block|,
operator|new
name|Functor
argument_list|(
literal|"getConsumerSeq"
argument_list|)
block|,
operator|new
name|Functor
argument_list|(
literal|"getProdName"
argument_list|)
block|,
operator|new
name|Functor
argument_list|(
literal|"getProducerSeq"
argument_list|)
block|,
operator|new
name|Functor
argument_list|(
literal|"getMsgBody"
argument_list|)
block|}
argument_list|,
operator|new
name|Functor
index|[]
block|{
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|}
argument_list|,
operator|new
name|Class
index|[]
block|{
name|String
operator|.
name|class
block|,
name|Integer
operator|.
name|class
block|,
name|String
operator|.
name|class
block|,
name|Integer
operator|.
name|class
block|,
name|String
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return Label key to get from label resource      */
specifier|public
name|String
name|getLabelResource
parameter_list|()
block|{
return|return
literal|"view_cons_sys_results_in_table"
return|;
block|}
comment|/**      * @param res SampleResult from the JMeter Sampler      */
specifier|public
name|void
name|add
parameter_list|(
name|SampleResult
name|res
parameter_list|)
block|{
name|Thread
name|timer
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|timerLoop
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|timer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * clear/resets the field.      */
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Clear called"
argument_list|,
operator|new
name|Exception
argument_list|(
literal|"Debug"
argument_list|)
argument_list|)
expr_stmt|;
name|model
operator|.
name|clearData
argument_list|()
expr_stmt|;
name|repaint
argument_list|()
expr_stmt|;
block|}
comment|/**      * Initialize the User Interface      */
specifier|private
name|void
name|init
parameter_list|()
block|{
name|this
operator|.
name|setLayout
argument_list|(
operator|new
name|BorderLayout
argument_list|()
argument_list|)
expr_stmt|;
comment|// Main Panel
name|JPanel
name|mainPanel
init|=
operator|new
name|JPanel
argument_list|()
decl_stmt|;
name|Border
name|margin
init|=
operator|new
name|EmptyBorder
argument_list|(
literal|10
argument_list|,
literal|10
argument_list|,
literal|5
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|mainPanel
operator|.
name|setBorder
argument_list|(
name|margin
argument_list|)
expr_stmt|;
name|mainPanel
operator|.
name|setLayout
argument_list|(
operator|new
name|VerticalLayout
argument_list|(
literal|5
argument_list|,
name|VerticalLayout
operator|.
name|LEFT
argument_list|)
argument_list|)
expr_stmt|;
comment|// Name
name|mainPanel
operator|.
name|add
argument_list|(
name|makeTitlePanel
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set up the table itself
name|table
operator|=
operator|new
name|JTable
argument_list|(
name|model
argument_list|)
expr_stmt|;
comment|// table.getTableHeader().setReorderingAllowed(false);
name|tableScrollPanel
operator|=
operator|new
name|JScrollPane
argument_list|(
name|table
argument_list|)
expr_stmt|;
name|tableScrollPanel
operator|.
name|setViewportBorder
argument_list|(
name|BorderFactory
operator|.
name|createEmptyBorder
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set up footer of table which displays the messages
name|JPanel
name|messagePanel
init|=
operator|new
name|JPanel
argument_list|()
decl_stmt|;
name|JLabel
name|messageLabel
init|=
operator|new
name|JLabel
argument_list|(
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"graph_results_message"
argument_list|)
argument_list|)
decl_stmt|;
name|messageLabel
operator|.
name|setForeground
argument_list|(
name|Color
operator|.
name|black
argument_list|)
expr_stmt|;
name|messageField
operator|=
operator|new
name|JTextField
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|messageField
operator|.
name|setBorder
argument_list|(
name|BorderFactory
operator|.
name|createEmptyBorder
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|messageField
operator|.
name|setEditable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|messageField
operator|.
name|setForeground
argument_list|(
name|Color
operator|.
name|black
argument_list|)
expr_stmt|;
name|messageField
operator|.
name|setBackground
argument_list|(
name|getBackground
argument_list|()
argument_list|)
expr_stmt|;
name|messagePanel
operator|.
name|add
argument_list|(
name|messageLabel
argument_list|)
expr_stmt|;
name|messagePanel
operator|.
name|add
argument_list|(
name|messageField
argument_list|)
expr_stmt|;
comment|// Set up info Panel table
name|JPanel
name|tableMsgPanel
init|=
operator|new
name|JPanel
argument_list|()
decl_stmt|;
name|tableMsgPanel
operator|.
name|setLayout
argument_list|(
operator|new
name|FlowLayout
argument_list|()
argument_list|)
expr_stmt|;
name|tableMsgPanel
operator|.
name|setBorder
argument_list|(
name|BorderFactory
operator|.
name|createEmptyBorder
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|tableMsgPanel
operator|.
name|add
argument_list|(
name|messagePanel
argument_list|)
expr_stmt|;
comment|// Set up the table with footer
name|JPanel
name|tablePanel
init|=
operator|new
name|JPanel
argument_list|()
decl_stmt|;
name|tablePanel
operator|.
name|setLayout
argument_list|(
operator|new
name|BorderLayout
argument_list|()
argument_list|)
expr_stmt|;
name|tablePanel
operator|.
name|add
argument_list|(
name|tableScrollPanel
argument_list|,
name|BorderLayout
operator|.
name|CENTER
argument_list|)
expr_stmt|;
name|tablePanel
operator|.
name|add
argument_list|(
name|tableMsgPanel
argument_list|,
name|BorderLayout
operator|.
name|SOUTH
argument_list|)
expr_stmt|;
comment|// Add the main panel and the graph
name|this
operator|.
name|add
argument_list|(
name|mainPanel
argument_list|,
name|BorderLayout
operator|.
name|NORTH
argument_list|)
expr_stmt|;
name|this
operator|.
name|add
argument_list|(
name|tablePanel
argument_list|,
name|BorderLayout
operator|.
name|CENTER
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Gets the number of processed messages.      */
specifier|protected
specifier|synchronized
name|void
name|timerLoop
parameter_list|()
block|{
name|Map
name|ProducerTextMap
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|Map
name|currentProducerMap
init|=
literal|null
decl_stmt|;
name|String
name|ProducerName
init|=
literal|null
decl_stmt|;
name|String
name|MsgBody
init|=
literal|null
decl_stmt|;
name|String
name|ConsumerName
init|=
literal|null
decl_stmt|;
name|String
name|ProdSequenceNo
init|=
literal|null
decl_stmt|;
name|String
name|mapKey
init|=
literal|null
decl_stmt|;
name|int
name|expectedNoOfMessages
init|=
name|ConsumerSysTest
operator|.
name|noOfMessages
decl_stmt|;
name|int
name|consumerCount
init|=
name|ConsumerSysTest
operator|.
name|ConsumerCount
decl_stmt|;
name|boolean
name|dowhile
init|=
literal|true
decl_stmt|;
name|Map
name|consumerMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
name|Map
name|prodNameMap
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
name|Map
name|prodMsgMap
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
while|while
condition|(
name|dowhile
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|ConsumerSysTest
name|consumer
init|=
operator|new
name|ConsumerSysTest
argument_list|()
decl_stmt|;
name|currentProducerMap
operator|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
name|consumer
operator|.
name|resetProducerMap
argument_list|()
argument_list|)
expr_stmt|;
comment|//System.out.println("CURR MAP = " + currentProducerMap);
comment|//            ConsumerSysTest.ProducerMap.clear();
if|if
condition|(
name|currentProducerMap
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|dowhile
operator|=
literal|false
expr_stmt|;
block|}
comment|// Put the map values to another map for parsing.
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|currentProducerMap
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|ProdMsg
init|=
operator|(
name|String
operator|)
name|currentProducerMap
operator|.
name|get
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
comment|//System.out.println("");
name|ProducerName
operator|=
name|ProdMsg
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|ProdMsg
operator|.
name|indexOf
argument_list|(
literal|"#"
argument_list|)
argument_list|)
expr_stmt|;
name|MsgBody
operator|=
name|ProdMsg
operator|.
name|substring
argument_list|(
name|ProdMsg
operator|.
name|indexOf
argument_list|(
literal|"#"
argument_list|)
operator|+
literal|1
argument_list|,
name|ProdMsg
operator|.
name|indexOf
argument_list|(
literal|"#"
argument_list|,
name|ProdMsg
operator|.
name|indexOf
argument_list|(
literal|"#"
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|ProdSequenceNo
operator|=
name|ProdMsg
operator|.
name|substring
argument_list|(
name|ProdMsg
operator|.
name|indexOf
argument_list|(
literal|"#"
argument_list|,
name|ProdMsg
operator|.
name|indexOf
argument_list|(
literal|"#"
argument_list|,
name|ProdMsg
operator|.
name|indexOf
argument_list|(
literal|"#"
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
operator|+
literal|1
argument_list|,
name|ProdMsg
operator|.
name|lastIndexOf
argument_list|(
literal|"#"
argument_list|)
argument_list|)
expr_stmt|;
name|ConsumerName
operator|=
name|ProdMsg
operator|.
name|substring
argument_list|(
name|ProdMsg
operator|.
name|lastIndexOf
argument_list|(
literal|"#"
argument_list|)
operator|+
literal|1
argument_list|,
name|ProdMsg
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|ConsumerSysTest
operator|.
name|destination
condition|)
block|{
name|mapKey
operator|=
name|ConsumerName
operator|+
name|ProducerName
expr_stmt|;
block|}
else|else
block|{
name|mapKey
operator|=
name|ProducerName
expr_stmt|;
block|}
if|if
condition|(
name|ProducerTextMap
operator|.
name|containsKey
argument_list|(
name|mapKey
argument_list|)
condition|)
block|{
comment|// Increment the counter value
name|Integer
name|value
init|=
operator|(
name|Integer
operator|)
name|ProducerTextMap
operator|.
name|get
argument_list|(
name|mapKey
argument_list|)
decl_stmt|;
name|ProducerTextMap
operator|.
name|put
argument_list|(
name|mapKey
argument_list|,
operator|new
name|Integer
argument_list|(
name|value
operator|.
name|intValue
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Put the Producer Name in the map
name|ProducerTextMap
operator|.
name|put
argument_list|(
name|mapKey
argument_list|,
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Integer
name|ConsumerSeqID
init|=
operator|(
name|Integer
operator|)
name|ProducerTextMap
operator|.
name|get
argument_list|(
name|mapKey
argument_list|)
decl_stmt|;
name|Integer
name|ProducerSeqID
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|ProdSequenceNo
argument_list|)
decl_stmt|;
if|if
condition|(
name|ConsumerSysTest
operator|.
name|destination
condition|)
block|{
comment|// Check for duplicate message.
if|if
condition|(
name|ConsumerSeqID
operator|.
name|intValue
argument_list|()
operator|>
name|expectedNoOfMessages
condition|)
block|{
name|messageField
operator|.
name|setText
argument_list|(
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"duplicate_message"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|MsgBody
operator|.
name|equals
argument_list|(
name|ProducerSysTest
operator|.
name|LAST_MESSAGE
argument_list|)
condition|)
block|{
comment|// Check for message order.
if|if
condition|(
name|ConsumerSeqID
operator|.
name|intValue
argument_list|()
operator|!=
name|expectedNoOfMessages
condition|)
block|{
name|messageField
operator|.
name|setText
argument_list|(
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"not_in_order_message"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentProducerMap
operator|.
name|size
argument_list|()
operator|==
name|i
condition|)
block|{
if|if
condition|(
name|messageField
operator|.
name|getText
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|messageField
operator|.
name|setText
argument_list|(
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"system_test_pass"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
comment|//Create map for each consumer
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|consumerCount
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|consumerMap
operator|.
name|containsKey
argument_list|(
operator|new
name|String
argument_list|(
name|ConsumerName
argument_list|)
argument_list|)
condition|)
block|{
name|consumerMap
operator|.
name|put
argument_list|(
operator|new
name|String
argument_list|(
name|ConsumerName
argument_list|)
argument_list|,
operator|new
name|LinkedHashMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|//create Producer Name Map
if|if
condition|(
operator|!
name|prodNameMap
operator|.
name|containsKey
argument_list|(
name|ProducerName
argument_list|)
condition|)
block|{
name|prodNameMap
operator|.
name|put
argument_list|(
name|ProducerName
argument_list|,
operator|(
literal|null
operator|)
argument_list|)
expr_stmt|;
block|}
comment|//Get the current size of consumer
name|int
name|seqVal
init|=
literal|0
decl_stmt|;
name|Object
index|[]
name|cObj
init|=
name|consumerMap
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|cObj
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
name|String
name|cMapKey
init|=
operator|(
name|String
operator|)
name|cObj
index|[
name|k
index|]
decl_stmt|;
name|Map
name|cMapVal
init|=
operator|(
name|Map
operator|)
name|consumerMap
operator|.
name|get
argument_list|(
name|cObj
index|[
name|k
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|cMapKey
operator|.
name|equals
argument_list|(
name|ConsumerName
argument_list|)
condition|)
block|{
name|seqVal
operator|=
name|cMapVal
operator|.
name|size
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
comment|//Put object to its designated consumer map
name|Object
index|[]
name|consumerObj
init|=
name|consumerMap
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|consumerObj
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|String
name|cMapKey
init|=
operator|(
name|String
operator|)
name|consumerObj
index|[
name|j
index|]
decl_stmt|;
name|Map
name|cMapVal
init|=
operator|(
name|LinkedHashMap
operator|)
name|consumerMap
operator|.
name|get
argument_list|(
name|consumerObj
index|[
name|j
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|cMapKey
operator|.
name|equals
argument_list|(
name|ConsumerName
argument_list|)
condition|)
block|{
name|cMapVal
operator|.
name|put
argument_list|(
operator|new
name|Integer
argument_list|(
name|seqVal
argument_list|)
argument_list|,
operator|(
name|ProducerName
operator|+
literal|"/"
operator|+
name|ProducerSeqID
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Add data to table row
if|if
condition|(
name|ConsumerSysTest
operator|.
name|destination
condition|)
block|{
name|SystemTestMsgSample
name|msgSample
init|=
operator|new
name|SystemTestMsgSample
argument_list|(
name|ConsumerName
argument_list|,
name|ProducerName
argument_list|,
name|MsgBody
argument_list|,
name|ProducerSeqID
argument_list|,
name|ConsumerSeqID
argument_list|)
decl_stmt|;
name|model
operator|.
name|addRow
argument_list|(
name|msgSample
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|msgKey
init|=
name|ConsumerName
operator|+
literal|"#"
operator|+
name|ProducerName
operator|+
literal|"#"
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|ProducerSeqID
argument_list|)
decl_stmt|;
name|String
name|msgVal
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|ConsumerSeqID
argument_list|)
operator|+
literal|"#"
operator|+
name|MsgBody
decl_stmt|;
if|if
condition|(
operator|!
name|prodMsgMap
operator|.
name|containsKey
argument_list|(
name|msgKey
argument_list|)
condition|)
block|{
name|prodMsgMap
operator|.
name|put
argument_list|(
operator|(
name|msgKey
operator|)
argument_list|,
operator|(
name|msgVal
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
operator|!
name|ConsumerSysTest
operator|.
name|destination
condition|)
block|{
comment|//Validate message sequence
name|validateMsg
argument_list|(
name|prodNameMap
argument_list|,
name|consumerMap
argument_list|)
expr_stmt|;
comment|//Populate msg sample
name|populateMsgSample
argument_list|(
name|prodMsgMap
argument_list|)
expr_stmt|;
if|if
condition|(
name|msgNotOrdered
condition|)
block|{
name|messageField
operator|.
name|setText
argument_list|(
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"not_in_order_message"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|messageField
operator|.
name|setText
argument_list|(
name|JMeterUtils
operator|.
name|getResString
argument_list|(
literal|"system_test_pass"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|boolean
name|validateMsg
parameter_list|(
name|Map
name|prodNameMap
parameter_list|,
name|Map
name|cMap
parameter_list|)
block|{
name|Object
index|[]
name|cObj
init|=
name|cMap
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|cObj
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|Map
name|childMap
init|=
operator|(
name|Map
operator|)
name|cMap
operator|.
name|get
argument_list|(
name|cObj
index|[
name|j
index|]
argument_list|)
decl_stmt|;
name|Object
index|[]
name|nameObj
init|=
name|prodNameMap
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
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
name|nameObj
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|prodName
init|=
operator|(
name|String
operator|)
name|nameObj
index|[
name|i
index|]
decl_stmt|;
name|String
name|tempProdHolder
init|=
literal|null
decl_stmt|;
name|String
name|tempProdIDHolder
init|=
literal|null
decl_stmt|;
name|Object
index|[]
name|childObj
init|=
name|childMap
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|childObj
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
name|Integer
name|childMapKey
init|=
operator|(
name|Integer
operator|)
name|childObj
index|[
name|k
index|]
decl_stmt|;
name|String
name|childMapVal
init|=
operator|(
name|String
operator|)
name|childMap
operator|.
name|get
argument_list|(
name|childObj
index|[
name|k
index|]
argument_list|)
decl_stmt|;
name|String
name|prodVal
init|=
name|childMapVal
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|childMapVal
operator|.
name|indexOf
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|prodIDVal
init|=
name|childMapVal
operator|.
name|substring
argument_list|(
name|childMapVal
operator|.
name|indexOf
argument_list|(
literal|"/"
argument_list|)
operator|+
literal|1
argument_list|,
name|childMapVal
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|prodVal
operator|.
name|equals
argument_list|(
name|prodName
argument_list|)
condition|)
block|{
if|if
condition|(
name|tempProdHolder
operator|==
literal|null
condition|)
block|{
name|tempProdHolder
operator|=
name|prodVal
expr_stmt|;
name|tempProdIDHolder
operator|=
name|prodIDVal
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|prodIDVal
argument_list|)
operator|>
name|Integer
operator|.
name|parseInt
argument_list|(
name|tempProdIDHolder
argument_list|)
condition|)
block|{
name|tempProdHolder
operator|=
name|prodVal
expr_stmt|;
name|tempProdIDHolder
operator|=
name|prodIDVal
expr_stmt|;
block|}
else|else
block|{
name|msgNotOrdered
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
else|else
block|{
continue|continue;
block|}
block|}
block|}
block|}
return|return
name|msgNotOrdered
return|;
block|}
specifier|private
name|void
name|populateMsgSample
parameter_list|(
name|Map
name|msgMap
parameter_list|)
block|{
name|Object
index|[]
name|msgObj
init|=
name|msgMap
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
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
name|msgObj
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|mapKey
init|=
operator|(
name|String
operator|)
name|msgObj
index|[
name|i
index|]
decl_stmt|;
name|String
name|mapVal
init|=
operator|(
name|String
operator|)
name|msgMap
operator|.
name|get
argument_list|(
name|msgObj
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|String
name|ConsumerName
init|=
name|mapKey
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|mapKey
operator|.
name|indexOf
argument_list|(
literal|"#"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|ProducerName
init|=
name|mapKey
operator|.
name|substring
argument_list|(
name|mapKey
operator|.
name|indexOf
argument_list|(
literal|"#"
argument_list|)
operator|+
literal|1
argument_list|,
name|mapKey
operator|.
name|indexOf
argument_list|(
literal|"#"
argument_list|,
name|mapKey
operator|.
name|lastIndexOf
argument_list|(
literal|"#"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|ProdSequenceNo
init|=
name|mapKey
operator|.
name|substring
argument_list|(
name|mapKey
operator|.
name|lastIndexOf
argument_list|(
literal|"#"
argument_list|)
operator|+
literal|1
argument_list|,
name|mapKey
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
empty_stmt|;
name|String
name|MsgKey
init|=
name|mapVal
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|mapVal
operator|.
name|indexOf
argument_list|(
literal|"#"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|MsgBody
init|=
name|mapVal
operator|.
name|substring
argument_list|(
name|mapVal
operator|.
name|indexOf
argument_list|(
literal|"#"
argument_list|)
operator|+
literal|1
argument_list|,
name|mapVal
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|Integer
name|ConsumerSeqID
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|MsgKey
argument_list|)
decl_stmt|;
name|Integer
name|ProducerSeqID
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|ProdSequenceNo
argument_list|)
decl_stmt|;
name|SystemTestMsgSample
name|msgSample
init|=
operator|new
name|SystemTestMsgSample
argument_list|(
name|ConsumerName
argument_list|,
name|ProducerName
argument_list|,
name|MsgBody
argument_list|,
name|ProducerSeqID
argument_list|,
name|ConsumerSeqID
argument_list|)
decl_stmt|;
name|model
operator|.
name|addRow
argument_list|(
name|msgSample
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit


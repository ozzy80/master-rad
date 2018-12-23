import pandas as pd
import sys
import matplotlib.pyplot as plt

def clean_csv(df):
    columns = df['Source'].str.startswith('192.168') & df['Destination'].str.startswith('192.168')
    return df[columns]
	


df = clean_csv(pd.read_csv(sys.argv[1]))

receive_packet_df = df[df['Destination'] == '192.168.0.128']
total_receive = receive_packet_df['Length'].sum()
print('Ukupno dobijeno podataka: {} bajtova'.format(total_receive))

sent_packet_df = df[df['Source'] == '192.168.0.128']
total_sent = sent_packet_df['Length'].sum()
print('Ukupno poslato podataka: {} bajtova'.format(total_sent))

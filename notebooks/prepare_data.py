import numpy as np

import codecs
import json

def unwrap_lucene_features(name, row):
    doc_scores = ['qScores', 'aScores', 'bothQAScores', 'bothQAScoresMustHave']
    doc_ids = ['qDocs', 'aDocs', 'bothQADoc', 'bothQADocAMustHave']

    d = row[name]

    for scores, docs in zip(doc_scores, doc_ids):
        d_scores = d[scores]
        std = np.std(d_scores)
        d[scores + '_mean'] = np.mean(d_scores)
        d[scores + '_std'] = std
        d[scores + '_median'] = np.median(d_scores)
        d[scores + '_doc_scores'] = zip(d[docs], d_scores)

        for i, score in enumerate(d_scores):
            d['%s_doc_%d' % (scores, i + 1)] = score
            d['%s_doc_%d_up' % (scores, i + 1)] = score + std
            d['%s_doc_%d_down' % (scores, i + 1)] = score - std

    blacklist = set(doc_scores + doc_ids)
    tuples = [(name + '_' + n, f) for (n, f) in d.items() if n not in blacklist]

    return dict(tuples)

dicts = []

lucene_features = ['wiki_ck12', 'ck12_ebook', 'ck12_ebook_ngrams', 'wiki_ck12_ngrams', 'wiki_full']

def transform(row):
    for lucene_feature in lucene_features:
        lfs = unwrap_lucene_features(lucene_feature, row['luceneFeatures'])
        row.update(lfs)

    w2v_features = row['word2VecFeatures']
    row['w2v_sim'] = w2v_features['word2vecCosine'] if w2v_features else 0.0

    del row['luceneFeatures']
    del row['word2VecFeatures']
    return row

with codecs.open('/home/agrigorev/git-projects/allen-qa/lucene-features-6.json', 'r', 'utf-8') as f:
    for line in f:
        if '"source":"VALIDATION"' in line:
            continue
        row = json.loads(line)
        row = transform(row)
        dicts.append(row)

with codecs.open('lucene-features-6-processed.json', 'w', 'utf-8') as f:
    for d in dicts:
        f.write(json.dumps(d))
        f.write('\n')